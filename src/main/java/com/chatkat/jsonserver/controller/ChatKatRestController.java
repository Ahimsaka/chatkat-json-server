package com.chatkat.jsonserver.controller;

import com.chatkat.jsonserver.dataobjects.Channel;
import com.chatkat.jsonserver.dataobjects.Guild;
import com.chatkat.jsonserver.dataobjects.directory.Directory;
import com.chatkat.jsonserver.service.ChannelBuilder;
import com.chatkat.jsonserver.service.GuildBuilder;
import com.chatkat.jsonserver.service.directory.DirectoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ChatKatRestController {
    @Autowired
    GuildBuilder guildBuilder;
    @Autowired
    ChannelBuilder channelBuilder;
    @Autowired
    DirectoryBuilder directoryBuilder;

    @GetMapping(value={"/", "/dir", "/directory", "/guild", "/channel", "/guilds","/channels"})
    public Directory directory(){
        return directoryBuilder.build();
    }

    @GetMapping("/guild/{id}")
    public Guild guild(@PathVariable("id") long id, @RequestParam(value = "last", defaultValue="") String last) {
        return guildBuilder.build(id);
    }

    @GetMapping("/channel/{id}")
    public Channel channel(@PathVariable("id") long id) {
        return channelBuilder.build(id);
    }
}
