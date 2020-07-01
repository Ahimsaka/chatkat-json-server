package com.chatkat.rest;

import com.chatkat.rest.dataobjects.Guild;
import com.chatkat.rest.dataobjects.UserResult;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@EnableAutoConfiguration
public class AppCommandLineRunner implements CommandLineRunner {
    @Autowired
    Logger log;
    @Autowired
    private InfluxDB influxDb;
    @Autowired
    InfluxDBMapper influxDBMapper;
    @Autowired
    private WebClient webClient;

    @Override
    public void run(String... args) {
        String guildId = "631868491955175540";
        log.info("Running");


        QueryResult qr = influxDb.query(new Query(String.format("SELECT authorID, sum FROM (SELECT sum(\"isValid\") FROM g%s GROUP BY authorID)", guildId)));
        List<UserResult> results = influxDBMapper.toPOJO(qr, UserResult.class, String.format("g%s", guildId));
        Guild guild = webClient.get().uri(String.format("/guilds/%s?with_counts=true", guildId)).retrieve().bodyToMono(Guild.class).block();
        guild.setMembers(webClient, results);
        log.info(guild.toString());
    }


}
