package com.chatkat.rest.service.influxdb;

import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class InfluxDBHandler {
    // restore if influxdbmapper pull request is accepted
    //@Autowired private InfluxDBMapper influxDBMapper;
    // remove if influxdbmapper pull request is accepted
    @Autowired private InfluxDBMapperTemp influxDBMapper;

    public Map<Long, Integer> getUserMap(String queryString, String guildId) {
        return Flux.fromIterable(
                influxDBMapper.query(new Query(queryString), UserRecord.class, String.format("g%s", guildId)))
                .collectMap(UserRecord::getKey, UserRecord::getSum).block();
    }
}
