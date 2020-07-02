package com.chatkat.rest.service.influxdb;

import org.influxdb.annotation.Column;

public class UserResult {
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
    public String getAuthorID(){return authorID.substring(1);}

}
