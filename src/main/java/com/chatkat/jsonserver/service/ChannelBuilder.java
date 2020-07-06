package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.Channel;
import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Objects;

@Service
// build Channel object with user scores.
public class ChannelBuilder {
    @Autowired
    private WebClient webClient;
    @Autowired
    private UserArrayBuilder userArrayBuilder;

    public Channel build(final long channelId) throws NullPointerException, WebClientResponseException {
        // get Channel object with metadata from discord
        Channel channel = Objects.requireNonNull(channelData(channelId));

        // set Discord Query to variable.
        Query query = new Query(String.format("SELECT * FROM (SELECT sum(\"isValid\") FROM g%d WHERE channelID = 'c%d') GROUP BY authorID", channel.getGuild_id(), channelId));

        // Set channel user scores.
        channel.setUsers(userArrayBuilder.findUsers(query, channel.getGuild_id()));
        return channel;
    }

    // create Channel object with populated metadata from Discord API.
    public Channel channelData(final long channelId) throws WebClientResponseException {
        return webClient.get()
                .uri(String.format("/channels/%d", channelId))
                .retrieve()
                .bodyToMono(Channel.class)
                .block();
    }
}
