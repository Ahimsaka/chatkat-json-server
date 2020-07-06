package com.chatkat.jsonserver.dataobjects;

import org.influxdb.annotation.Column;

/* Class for InfluxDBMapper to map output.
*
* Typically would be Annotated with @Measurement, but ChatKat bot uses
* the id of the guild in which the message is recorded as the measurement name
* and skipping annotation allows variable measurement.
* (see Application.java defined Bean influxDBMapper) */
public class UserRecord {
    @Column(name = "sum")
    private int sum;
    @Column(name = "authorID")
    private String authorID;

    public void setSum(Double sum) {
        this.sum = sum.intValue();
    }
    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public int getSum() {
        return sum;
    }
    public String getAuthorID() {
        return authorID;
    }
    public long getKey(){return Long.parseLong(authorID.substring(1));}

}
