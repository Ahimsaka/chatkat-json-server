package com.chatkat.jsonserver.dataobjects.directory;

import java.util.List;

public class Directory {
    List<GuildMap> guilds;

    @Override
    public String toString(){
        return String.format("{guilds: %s}", guilds.toString());
    }

    public void setGuilds(List<GuildMap> guilds) {
        this.guilds = guilds;
    }

    public List<GuildMap> getGuilds() {
        return guilds;
    }
}
