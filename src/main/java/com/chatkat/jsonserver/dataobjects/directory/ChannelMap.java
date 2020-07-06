package com.chatkat.jsonserver.dataobjects.directory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelMap {
    String name;
    long id;

    public ChannelMap(){
    }

    @Override
    public String toString(){
        return String.format("{name: %s, id: %d}", name, id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public long getId() {
        return this.id;
    }
}
