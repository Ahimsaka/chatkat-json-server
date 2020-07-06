package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.User;
import com.chatkat.jsonserver.dataobjects.UserRecord;
import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.*;

@Service
// Service creates Array of User Objects and is used by both GuildBuilder and ChannelBuilder
public class UserArrayBuilder {
    @Autowired
    /* GuildBuilder Bean is only used in methods called from ChannelBuilder. It still may be better
    * to extract the code used there to a separate service to eliminate circular dependency. */
    private GuildBuilder guildbuilder;
    @Autowired
    private InfluxDBMapperTemp influxDBMapper;
    @Autowired
    private WebClient webClient;

    // create Map of User Scores returned by the Query.
    private Map<Long, Integer> getUserMessageCountsMap(final Query query, final long guildId) {
        return Flux.fromIterable(
                influxDBMapper.query(query, UserRecord.class, String.format("g%d", guildId)))
                .collectMap(UserRecord::getKey, UserRecord::getSum).block();
    }

    /* findUsers signature called by ChannelBuilder retrieves approximate_member_count and uses it to call
    * the standard signature of findUsers*/
    public List<User> findUsers(final Query query, final long guildId) {
        int approximate_member_count = guildbuilder.guildData(guildId).getApproximate_member_count();
        return findUsers(query, guildId, approximate_member_count);
    }

    // Used by both ChannelBuilder and GuildBuilder to populate user list.
    public List<User> findUsers (final Query query, final long guildId, final int approximate_member_count) throws NullPointerException {
        // Store Map of User scores found in InfluxDB
        Map<Long, Integer> userMap =
                Objects.requireNonNull(getUserMessageCountsMap(query, guildId), "Guild not found in database.");

        // Init empty list set to the size of the guild approximate member count.
        List<User> userMetadata = new ArrayList<>(approximate_member_count);

        /* discord API only allows use to retrieve metadata for up to 1000 users at once.
        * For loop repeats call in case of guild with > 1000 users. */
        for (int i = 0; i < approximate_member_count; i+=1000){
            List<User> tempUsers = webClient.get()
                    // default value for limit parameter is 1, so must be set.
                    .uri(String.format("https://discord.com/api/guilds/%s/members?limit=1000&after=%d", guildId, i))
                    .retrieve()
                    // retrieve results as a flux of User objects
                    .bodyToFlux(User.class)
                    // filter out users that aren't in the database results
                    .filter(user -> userMap.containsKey(user.getId()))
                    // set the score for each user object
                    .map(user -> {
                        user.setSum(userMap.get(user.getId()));
                        return user;
                    })
                    .collectList().block();
            // if the batch isn't empty or null, add it to the List.
            if (!(tempUsers == null) && !tempUsers.isEmpty()) userMetadata.addAll(tempUsers);
        }

        userMetadata.sort(Comparator.comparingInt(User::getSum).reversed());
        return userMetadata;
    }

}
