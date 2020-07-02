package com.chatkat.rest.service.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class InfluxDBService {
    @Autowired
    private InfluxDB influxDb;
    @Autowired
    private InfluxDBMapper influxDBMapper;

    public Map<String, Integer> getUserMap(String queryString, String guildId) {
        QueryResult queryResult = influxDb.query(new Query(String.format(queryString, guildId)));

        return Flux.fromIterable(
                influxDBMapper.toPOJO(queryResult, UserResult.class, String.format("g%s", guildId)))
                .collectMap(UserResult::getAuthorID, UserResult::getSum).block();
    }
}
