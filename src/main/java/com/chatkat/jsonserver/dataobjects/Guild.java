package com.chatkat.jsonserver.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Guild {
    private String name;
    private long id;
    private int approximate_member_count;
    private List<User> users;

    @Override
    public String toString(){
        return String.format("Guild: %s, id: %d, members-total: %d, members: %s}", name, id, approximate_member_count, users.toString());
    }

    public void setUsers(List<User> users){
        this.users = users;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setApproximate_member_count(int approximate_member_count) {
        this.approximate_member_count = approximate_member_count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public int getApproximate_member_count() {
        return approximate_member_count;
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return users;
    }
}
