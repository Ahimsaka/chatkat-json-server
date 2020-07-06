package com.chatkat.jsonserver.dataobjects.directory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuildMap {
    String name;
    long id;
    List<ChannelMap> channels;

    public GuildMap(){
    }

    @Override
    public String toString(){
        return String.format("{name: %s, id: %d, channels: %s}", name, id, channels.toString());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setChannels(List<ChannelMap> channels) {
        this.channels = channels;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public List<ChannelMap> getChannels() {
        return channels;
    }
}