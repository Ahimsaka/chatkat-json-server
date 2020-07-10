package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.Channel;
import com.chatkat.jsonserver.dataobjects.Guild;
import com.chatkat.jsonserver.dataobjects.Directory;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
// Service builds map of Guilds and Channels recorded in influxDB
public class DirectoryService {
    Logger log = LoggerFactory.getLogger(DirectoryService.class);
    @Autowired
    InfluxDB influxDB;
    @Autowired
    DiscordApiWebClientService discordApiWebClientService;

    public Directory build() {
        return Directory.builder().guilds(guilds()).build();
    }

    public List<Guild> guilds() {
        /* use influxDB rather than influxDBMapper because influxDBMapper is not designed for
        * mapping schema to object. */
        return influxDB.query(new Query("SHOW TAG VALUES WITH KEY = channelID"))
                .getResults().stream()
                // unpack QueryResult to stream of Series
                .map(QueryResult.Result::getSeries)
                .flatMap(Collection::stream)
                // collect GuildMap objects
                .map(this::getGuild)
                .collect(Collectors.toList());
    }

    // accept influxDB-java QueryResult.Series and return GuildMap object with channels
    private Guild getGuild(final QueryResult.Series series){
        Guild guild = discordApiWebClientService.getGuildById(Long.parseLong(series.getName().substring(1)));
        //populate guild channel list
        guild.setChannels(getGuildChannels(series.getValues()));
        return guild;
    }

    /* accept the List<List<Object>> returned by series.getValues() called from getGuildMap()
    * and return list of ChannelMap objects */
    private List<Channel> getGuildChannels(List<List<Object>> values){
        return values.stream().map(channel -> Long.parseLong(channel.get(1).toString().substring(1)))
                // convert channelIds found in database to Channel Metadata
                .map(discordApiWebClientService::getChannelById)
                .collect(Collectors.toList());
    }

}
