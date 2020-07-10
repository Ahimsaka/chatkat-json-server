package com.chatkat.jsonserver.dataobjects;

import lombok.Data;
import org.influxdb.annotation.Column;

import javax.annotation.sql.DataSourceDefinitions;

/* Class for InfluxDBMapper to map output.
*
* Typically would be Annotated with @Measurement, but ChatKat bot uses
* the id of the guild in which the message is recorded as the measurement name
* and skipping annotation allows variable measurement.
* (see Application.java defined Bean influxDBMapper) */
@Data
public class UserRecord {
    @Column(name = "sum")
    private int sum;
    @Column(name = "authorID")
    private String authorID;

}
