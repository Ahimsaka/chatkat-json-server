package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.UserRecord;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.impl.InfluxDBMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/* Temporary InfluxDBMapper subclass extends InfluxDBMapper to use toPOJO signature in a
*  new .query() signature. This extension has been added as a pull request on influxdb-java
* and should be removed when pull request is accepted. */
@Service
public class InfluxDBService extends InfluxDBMapper {
    private Logger log = LoggerFactory.getLogger(InfluxDBService.class);
    private InfluxDB influxDB;

    public InfluxDBService(@Autowired final InfluxDB influxDB){
        super(influxDB);
        this.influxDB = influxDB.setDatabase("ChatKat");
    }
    public <T> List<T> query(final Query query, final Class<T> clazz, final String measurementName) {
        return toPOJO(influxDB.query(query), clazz, measurementName);
    }

    public Map<Long, Integer> getUserMessageCountsMap(final Query query, final long guildId) {
        return Flux.fromIterable(
                query(query, UserRecord.class, String.format("g%d", guildId)))
                .collectMap(this::getKey, UserRecord::getSum).block();
    }

    private long getKey(UserRecord userRecord) {
        return Long.parseLong(userRecord.getAuthorID().substring(1));
    }

}
