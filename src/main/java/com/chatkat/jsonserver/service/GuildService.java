package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.Channel;
import com.chatkat.jsonserver.dataobjects.Guild;
import com.chatkat.jsonserver.dataobjects.User;
import org.influxdb.dto.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Comparator;
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

    public Guild getById(final long guildId, final String time_frame) throws NullPointerException, WebClientResponseException {
        // build Guild object with metadata from discord API
        Guild guild = Objects.requireNonNull(discordApiWebClientService.getGuildById(guildId));

        // set Guild  user scores
        return setUserScores(guild);
    }

    private Guild setUserScores(final Guild guild) {
        // set Discord Query to variable.
        Query query = new Query(String.format(
                "SELECT * FROM (SELECT sum(\"isValid\") FROM g%d GROUP BY authorID)",
                guild.getId()));

        Map<Long, Integer> userScoreMap = influxDBService.getUserMessageCountsMap(query, guild.getId());

        guild.setUsers(discordApiWebClientService.getUsersByGuildId(guild.getId()).stream()
                .filter(user -> userScoreMap.containsKey(user.getId()))
                .peek(user -> user.setSum(userScoreMap.get(user.getId())))
                .sorted(Comparator.comparingInt(User::getSum).reversed())
                .collect(Collectors.toList()));

        return guild;
    }

}
