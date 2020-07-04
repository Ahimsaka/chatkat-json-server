package com.chatkat.rest.service;

import com.chatkat.rest.dataobjects.Channel;
import org.influxdb.dto.Query;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ChannelBuilder {
    @Autowired private WebClient webClient;
    @Autowired private UserArrayBuilder userArrayBuilder;


    public Channel build(String channelId) {
        Channel channel = channelData(channelId);

        Query query = new Query(String.format("SELECT * FROM (SELECT sum(\"isValid\") FROM g%d WHERE channelID = 'c%s') GROUP BY authorID", channel.getGuild_id(), channelId));

        channel.setUsers(userArrayBuilder.findUsers(query, channel.getGuild_id()));

        return channel;
    }

    public Channel channelData(String channelId){
        return webClient.get()
                .uri(String.format("/channels/%s", channelId))
                .retrieve()
                .bodyToMono(Channel.class)
                .block();
    }

}
