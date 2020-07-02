package com.chatkat.rest.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private long id;
    private String username;
    private String nick;
    private int sum;

    @Override
    public String toString(){
        return String.format("{name: %s, nick: %s, id: %d, sum: %d}", username, nick, id, sum);
    }

    @JsonProperty("user")
    private void unpack(Map<String, Object> user) {
        setId(Long.parseLong((String) user.get("id")));
        setUsername((String) user.get("username"));
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setSum(int sum) {
        this.sum = sum;
    }
    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }
    public long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public int getSum() {
        return sum;
    }
}
