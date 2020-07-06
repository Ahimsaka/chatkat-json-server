package com.chatkat.rest.controller;

import com.chatkat.rest.dataobjects.Channel;
import com.chatkat.rest.dataobjects.Guild;
import com.chatkat.rest.service.ChannelBuilder;
import com.chatkat.rest.service.GuildBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ChatKatRestController {
    @Autowired
    GuildBuilder guildBuilder;
    @Autowired
    ChannelBuilder channelBuilder;

    @GetMapping("/guild/{id}")
    public Guild guild(@PathVariable("id") long id) {
        return guildBuilder.build(id);
    }

    @GetMapping("/channel/{id}")
    public Channel channel(@PathVariable("id") long id) {
        return channelBuilder.build(id);
    }
}
