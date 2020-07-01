package com.chatkat.rest.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMeta{
    long id;
    String name;
    String nick;

    @Override
    public String toString(){
        return String.format("{name: %s, nick:, %s, id: %d}", name, nick, id);
    }

    @JsonProperty("user")
    private void unpack(Map<String, Object> user) {
        this.id =  Long.parseLong((String) user.get("id"));
        this.name = (String) user.get("username");
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
    public String getName() {
        return name;
    }

}
