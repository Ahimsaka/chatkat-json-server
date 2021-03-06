package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.Guild;
import com.chatkat.jsonserver.dataobjects.User;
import org.influxdb.dto.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
// build Guild object with user scores
public class GuildService {
    private Logger log = LoggerFactory.getLogger(GuildService.class);
    @Autowired
    private DiscordApiWebClientService discordApiWebClientService;
    @Autowired
    private InfluxDBService influxDBService;

    public Guild getById(final long guildId, final long last) throws NullPointerException, WebClientResponseException {
        // build Guild object with metadata from discord API
        Guild guild = Objects.requireNonNull(discordApiWebClientService.getGuildById(guildId));

        if (last != 0) guild.setMessages_recorded_since(Date.from(Instant.ofEpochMilli(last)));

        // set Guild  user scores
        return setUserScores(guild, last);
    }

    private Guild setUserScores(final Guild guild, final long last) {
        // set Discord Query to variable.
        Query query = new Query(String.format(
                "SELECT * FROM (SELECT sum(\"isValid\") FROM g%d WHERE time >= %dms GROUP BY authorID)",
                guild.getId(), last));

        Map<Long, Integer> userScoreMap = influxDBService.getUserMessageCountsMap(query, guild.getId());

        guild.setUsers(discordApiWebClientService.getUsersByGuildId(guild.getId()).stream()
                .filter(user -> userScoreMap.containsKey(user.getId()))
                .peek(user -> user.setSum(userScoreMap.get(user.getId())))
                .sorted(Comparator.comparingInt(User::getSum).reversed())
                .collect(Collectors.toList()));

        return guild;
    }

}
