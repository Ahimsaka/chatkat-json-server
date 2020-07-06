package com.chatkat.jsonserver.service.directory;

import com.chatkat.jsonserver.dataobjects.directory.ChannelMap;
import com.chatkat.jsonserver.dataobjects.directory.Directory;
import com.chatkat.jsonserver.dataobjects.directory.GuildMap;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
// Service builds map of Guilds and Channels recorded in influxDB
public class DirectoryBuilder {
    @Autowired
    InfluxDB influxDB;
    @Autowired
    WebClient webClient;

    public Directory build() {
        Directory directory = new Directory();
        directory.setGuilds(guilds());
        return directory;
    }

    public List<GuildMap> guilds() {
        /* use influxDB rather than influxDBMapper because influxDBMapper is not designed for
        * mapping schema to object. */
        return influxDB.query(new Query("SHOW TAG VALUES WITH KEY = channelID"))
                .getResults().stream()
                // unpack QueryResult to stream of Series
                .map(QueryResult.Result::getSeries)
                .flatMap(Collection::stream)
                // collect GuildMap objects
                .map(this::getGuildMap)
                .collect(Collectors.toList());
    }

    // accept influxDB-java QueryResult.Series and return GuildMap object with channels
    private GuildMap getGuildMap(final QueryResult.Series series){
        GuildMap guild = guildData(Long.parseLong(series.getName().substring(1)));
        //populate guild channel list
        guild.setChannels(guildChannels(series.getValues()));
        return guild;
    }

    // accept guild id found in database and return GuildMap object without channels
    public GuildMap guildData(final long guildId) throws NullPointerException {
        return Objects.requireNonNull(webClient.get()
                .uri(String.format("/guilds/%d?with_counts=true", guildId))
                .retrieve()
                .bodyToMono(GuildMap.class)
                .block(), "Could not retrieve guild from discord API.");
    }

    /* accept the List<List<Object>> returned by series.getValues() called from getGuildMap()
    * and return list of ChannelMap objects */
    private List<ChannelMap> guildChannels(List<List<Object>> values){
        return values.stream().map(channel -> Long.parseLong(channel.get(1).toString().substring(1)))
                // convert channelIds found in database to Channel Metadata
                .map(this::channelData)
                .collect(Collectors.toList());
    }

    // accept channel ID long and return ChannelMap object.
    public ChannelMap channelData(final long channelId) throws NullPointerException {
        return Objects.requireNonNull(webClient.get()
                .uri(String.format("/channels/%d", channelId))
                .retrieve()
                .bodyToMono(ChannelMap.class)
                .block(), "Could not retrieve channel from discord api.");
    }



}
