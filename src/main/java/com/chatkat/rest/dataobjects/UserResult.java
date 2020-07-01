package com.chatkat.rest.dataobjects;

import org.influxdb.annotation.Column;

public class UserResult {
    private long id;
    private String name;
    private String nick;
    @Column(name = "sum")
    private int sum;

    @Column(name = "authorID")
    private String authorID;

    public Boolean compareMeta(UserMeta userMeta){
        return authorID.substring(1).equals(String.valueOf(userMeta.getId()));
    }
    public UserResult setMeta(UserMeta userMeta){
        this.name = userMeta.getName();
        this.nick = userMeta.getNick();
        this.id = userMeta.getId();
        return this;
    }
    public void setSum(Double sum) {
        this.sum = sum.intValue();
    }
    public void setId(String id) {
        this.authorID = id;
    }

    public int getSum() {
        return sum;
    }
    public String getNick() {
        return nick;
    }
    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }



    @Override
    public String toString(){
        return String.format("{name: %s, nick: %s, id: %s, sum: %d}", getName(), getNick(), getId(), getSum());
    }

}
