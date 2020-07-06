package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.Channel;
import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Objects;

@Service
public class ChannelBuilder {
    @Autowired
    private WebClient webClient;
    @Autowired
    private UserArrayBuilder userArrayBuilder;

    public Channel build(final long channelId) throws NullPointerException, WebClientResponseException {
        Channel channel = Objects.requireNonNull(channelData(channelId));

        Query query = new Query(String.format("SELECT * FROM (SELECT sum(\"isValid\") FROM g%d WHERE channelID = 'c%d') GROUP BY authorID", channel.getGuild_id(), channelId));

        channel.setUsers(userArrayBuilder.findUsers(query, channel.getGuild_id()));

        return channel;
    }


    public Channel channelData(final long channelId) throws WebClientResponseException {
        return webClient.get()
                .uri(String.format("/channels/%d", channelId))
                .retrieve()
                .bodyToMono(Channel.class)
                .block();
    }

}
