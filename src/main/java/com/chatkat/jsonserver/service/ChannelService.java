package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.Channel;
import com.chatkat.jsonserver.dataobjects.User;
import io.netty.handler.codec.DateFormatter;
import org.influxdb.dto.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
// build Channel object with user scores.
public class ChannelService {
    private final Logger log = LoggerFactory.getLogger(ChannelService.class);
    @Autowired
    private InfluxDBService influxDBService;
    @Autowired
    private DiscordApiWebClientService discordApiWebClientService;

    public Channel getById(final long channelId, final long last) throws NullPointerException, WebClientResponseException {
        // get Channel object with metadata from discord
        Channel channel = Objects.requireNonNull(discordApiWebClientService.getChannelById(channelId));

        if (last != 0) channel.setMessages_recorded_since(Date.from(Instant.ofEpochMilli(last)));

        return setUserScores(channel, last);
    }

    private Channel setUserScores(final Channel channel, final long last) {
        // set Discord Query to variable.
        Query query = new Query(String.format(
                "SELECT * FROM (SELECT sum(\"isValid\") FROM g%d WHERE channelID = 'c%d' AND time >=%dms) GROUP BY authorID",
                channel.getGuild_id(), channel.getId(), last));

        Map<Long, Integer> userScoreMap = influxDBService.getUserMessageCountsMap(query, channel.getGuild_id());

        channel.setUsers(discordApiWebClientService.getUsersByGuildId(channel.getGuild_id()).stream()
                .filter(user -> userScoreMap.containsKey(user.getId()))
                .peek(user -> user.setSum(userScoreMap.get(user.getId())))
                .sorted(Comparator.comparingInt(User::getSum).reversed())
                .collect(Collectors.toList()));

        return channel;
    }
}
