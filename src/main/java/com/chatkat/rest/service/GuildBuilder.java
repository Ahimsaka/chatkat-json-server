package com.chatkat.rest.service;

import com.chatkat.rest.dataobjects.Guild;
import com.chatkat.rest.dataobjects.User;
import com.chatkat.rest.service.influxdb.InfluxDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class GuildBuilder {
    @Autowired
    private InfluxDBService influxDBService;
    @Autowired
    private WebClient webClient;

    private List<User> getUsers(Guild guild){
        String guildId = String.valueOf(guild.getId());
        int approximate_member_count = guild.getApproximate_member_count();
        return getUsers(guildId, approximate_member_count);
    }
    private List<User> getUsers(String guildId, int approximate_member_count){
        Map<String, Integer> dbResults =
                influxDBService.getUserMap("SELECT * FROM (SELECT sum(\"isValid\") FROM g%s GROUP BY authorID)", guildId);

        List <User> userMetadata = new ArrayList<>(approximate_member_count);
        for (int i = 0; i < approximate_member_count; i+=1000){
            userMetadata.addAll(
                    Objects.requireNonNull(webClient.get()
                    .uri(String.format("https://discord.com/api/guilds/%s/members?limit=1000&after=%d", guildId, i))
                    .retrieve()
                    .bodyToFlux(User.class)
                    .map(user -> {
                        String id = String.valueOf(user.getId());
                        if (dbResults.containsKey(id)) user.setSum(dbResults.get(id));
                        return user;
                    })
                    .collectList().block()));
        }
        userMetadata.sort(Comparator.comparingInt(User::getSum).reversed());
        return userMetadata;
    }

    public Guild build(String guildId){
        Guild guild = webClient.get().uri(String.format("/guilds/%s?with_counts=true", guildId)).retrieve().bodyToMono(Guild.class).block();
        guild.setUsers(getUsers(guild));
        return guild;
    }

}
