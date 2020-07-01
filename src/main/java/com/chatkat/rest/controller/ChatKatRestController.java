package com.chatkat.rest.controller;

import com.chatkat.rest.dataobjects.Guild;
import com.chatkat.rest.dataobjects.UserResult;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
public class ChatKatRestController {
    @Autowired
    InfluxDB influxDb;
    @Autowired
    InfluxDBMapper influxDBMapper;
    @Autowired
    private WebClient webClient;

    @GetMapping("/guild/{id}")
    public Guild guild(@PathVariable("id") String id) {
        QueryResult qr = influxDb.query(new Query(String.format("SELECT authorID, sum FROM (SELECT sum(\"isValid\") FROM g%s GROUP BY authorID)", id)));
        List<UserResult> results = influxDBMapper.toPOJO(qr, UserResult.class, String.format("g%s", id));

        Guild guild = webClient.get().uri(String.format("/guilds/%s?with_counts=true", id)).retrieve().bodyToMono(Guild.class).block();
        guild.setMembers(webClient, results);
        return guild;
    }
}
