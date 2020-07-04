package com.chatkat.rest.service;

import com.chatkat.rest.dataobjects.User;
import com.chatkat.rest.dataobjects.UserRecord;
import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class UserArrayBuilder {
    @Autowired private GuildBuilder guildbuilder;
    @Autowired private InfluxDBMapperTemp influxDBMapper;
    @Autowired private WebClient webClient;


    private Map<Long, Integer> getUserMessageCountsMap(final Query query, final long guildId) {
        return Flux.fromIterable(
                influxDBMapper.query(query, UserRecord.class, String.format("g%d", guildId)))
                .log()
                .collectMap(UserRecord::getKey, UserRecord::getSum).block();
    }


    public List<User> findUsers(final Query query, final long guildId){
        int approximate_member_count = guildbuilder.guildData(String.valueOf(guildId)).getApproximate_member_count();
        return findUsers(query, guildId, approximate_member_count);
    }


    public List<User> findUsers(final Query query, final long guildId, final int approximate_member_count){
        Map<Long, Integer> userMap =
                getUserMessageCountsMap(query, guildId);

        List<User> userMetadata = new ArrayList<>(approximate_member_count);

        for (int i = 0; i < approximate_member_count; i+=1000){
            List<User> tempUsers = webClient.get()
                    .uri(String.format("https://discord.com/api/guilds/%s/members?limit=1000&after=%d", guildId, i))
                    .retrieve()
                    .bodyToFlux(User.class)
                    .filter(user -> userMap.containsKey(user.getId()))
                    .map(user -> {
                        user.setSum(userMap.get(user.getId()));
                        return user;
                    })
                    .collectList().block();
            if (!(tempUsers == null) && !tempUsers.isEmpty()) userMetadata.addAll(tempUsers);
        }

        userMetadata.sort(Comparator.comparingInt(User::getSum).reversed());
        return userMetadata;
    }

}
