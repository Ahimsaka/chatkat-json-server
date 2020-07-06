package com.chatkat.rest.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {
    private String name;
    private long id;
    private long guild_id;
    private List<User> users;


    @Override
    public String toString(){
        return String.format("Channel: %s, id: %d, members: %s}", name, id, users.toString());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setGuild_id(long guild_id) {
        this.guild_id = guild_id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return users;
    }

    public long getGuild_id() {
        return guild_id;
    }
}
