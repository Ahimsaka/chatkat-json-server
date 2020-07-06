package com.chatkat.jsonserver.service;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class InfluxDBMapperTemp extends InfluxDBMapper {
    private InfluxDB influxDB;

    public InfluxDBMapperTemp(@Autowired final InfluxDB influxDB){
        super(influxDB);
        this.influxDB = influxDB;
    }
    public <T> List<T> query(final Query query, final Class<T> clazz, final String measurementName) {
        QueryResult queryResult = influxDB.query(query);
        return toPOJO(queryResult, clazz, measurementName);
    }
}
