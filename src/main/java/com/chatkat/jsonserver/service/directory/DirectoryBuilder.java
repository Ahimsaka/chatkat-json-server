package com.chatkat.jsonserver.service.directory;

import com.chatkat.jsonserver.dataobjects.Channel;
import com.chatkat.jsonserver.dataobjects.Guild;
import com.chatkat.jsonserver.dataobjects.directory.ChannelMap;
import com.chatkat.jsonserver.dataobjects.directory.Directory;
import com.chatkat.jsonserver.dataobjects.directory.GuildMap;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
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
        return influxDB.query(new Query("SHOW TAG VALUES WITH KEY = channelID"))
                .getResults().stream()
                .map(QueryResult.Result::getSeries)
                .flatMap(Collection::stream)
                .map(this::getGuildMap)
                .collect(Collectors.toList());
    }

    private GuildMap getGuildMap(final QueryResult.Series series){
        GuildMap guild = guildData(Long.parseLong(series.getName().substring(1)));
        guild.setChannels(guildChannels(series.getValues()));
        return guild;
    }

    public GuildMap guildData(final long guildId) throws NullPointerException {
        return Objects.requireNonNull(webClient.get()
                .uri(String.format("/guilds/%d?with_counts=true", guildId))
                .retrieve()
                .bodyToMono(GuildMap.class)
                .block(), "Could not retrieve guild from discord API.");
    }

    private List<ChannelMap> guildChannels(List<List<Object>> values){
        return values.stream().map(channel -> Long.parseLong(channel.get(1).toString().substring(1)))
                .map(this::channelData)
                .collect(Collectors.toList());
    }

    public ChannelMap channelData(final long channelId) throws NullPointerException {
        return Objects.requireNonNull(webClient.get()
                .uri(String.format("/channels/%d", channelId))
                .retrieve()
                .bodyToMono(ChannelMap.class)
                .block(), "Could not retrieve channel from discord api.");
    }



}
