package com.chatkat.rest.service;

import com.chatkat.rest.dataobjects.Channel;
import com.chatkat.rest.dataobjects.Guild;
import com.chatkat.rest.dataobjects.User;
import com.chatkat.rest.service.influxdb.InfluxDBHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ChannelBuilder {
    @Autowired private InfluxDBHandler influxDBHandler;
    @Autowired private WebClient webClient;
    @Autowired private Logger log;
    @Autowired private GuildBuilder guildBuilder;


    public Channel build(String channelId) {
        Channel channel = channelData(channelId);
        log.info(String.valueOf(channel.getGuild_id()));
        Guild guild = guildBuilder.guildData(String.valueOf(channel.getGuild_id()));
        log.info(guild.getName());
        channel.setUsers(findUsers(guild, channelId));
        return channel;
    }


    public Channel channelData(String channelId){
        return webClient.get()
                .uri(String.format("/channels/%s", channelId))
                .retrieve()
                .bodyToMono(Channel.class)
                .block();
    }

    private List<User> findUsers(Guild guild, String channelId){
        String guildId = String.valueOf(guild.getId());
        int approximate_member_count = guild.getApproximate_member_count();
        return findUsers(guildId, approximate_member_count, channelId);
    }
    private List<User> findUsers(String guildId, int approximate_member_count, String channelId){
        Map<Long, Integer> userMap =
                influxDBHandler.getUserMap(String.format("SELECT * FROM (SELECT sum(\"isValid\") FROM g%s WHERE channelID = 'c%s' GROUP BY authorID)", guildId, channelId), guildId);
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
