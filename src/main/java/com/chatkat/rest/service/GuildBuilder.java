package com.chatkat.rest.service;

import com.chatkat.rest.dataobjects.Guild;
import com.chatkat.rest.dataobjects.User;
import com.chatkat.rest.service.influxdb.InfluxDBHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class GuildBuilder {
    @Autowired private InfluxDBHandler influxDBHandler;
    @Autowired private WebClient webClient;
    @Autowired Logger log;

    // Build Guild POJO with List of User POJOs
    public Guild build(String guildId){
        Guild guild = guildData(guildId);
        guild.setUsers(findUsers(guild));
        return guild;
    }

    public Guild guildData(String guildId){
        return webClient.get()
                .uri(String.format("/guilds/%s?with_counts=true", guildId))
                .retrieve()
                .bodyToMono(Guild.class)
                .block();
    }

    private List<User> findUsers(Guild guild){
        String guildId = String.valueOf(guild.getId());
        int approximate_member_count = guild.getApproximate_member_count();
        return findUsers(guildId, approximate_member_count);
    }

    private List<User> findUsers(String guildId, int approximate_member_count){
        Map<Long, Integer> userMap =
                influxDBHandler.getUserMap(String.format("SELECT * FROM (SELECT sum(\"isValid\") FROM g%s GROUP BY authorID)", guildId), guildId);

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
