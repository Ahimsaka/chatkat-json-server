package com.chatkat.rest.controller;

import com.chatkat.rest.dataobjects.Guild;
import com.chatkat.rest.service.GuildBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatKatRestController {
    @Autowired
    GuildBuilder guildBuilder;

    @GetMapping("/guild/{id}")
    public Guild guild(@PathVariable("id") String id) {
        return guildBuilder.build(id);
    }
}
