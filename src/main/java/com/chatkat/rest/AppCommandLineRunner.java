package com.chatkat.rest;

import com.chatkat.rest.dataobjects.Channel;
import com.chatkat.rest.dataobjects.Guild;
import com.chatkat.rest.service.ChannelBuilder;
import com.chatkat.rest.service.GuildBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

@Component
@EnableAutoConfiguration
public class AppCommandLineRunner implements CommandLineRunner {
    @Autowired Logger log;
    @Autowired GuildBuilder guildBuilder;
    @Autowired ChannelBuilder channelBuilder;

    @Override
    public void run(String... args) {
        log.info("Running");

        String guildId = "631868491955175540";
        Guild guild = guildBuilder.build(guildId);
        log.info(guild.toString());
        String channelId = "631868491959369730";
        Channel channel = channelBuilder.build(channelId);
        log.info(channel.toString());
    }




}
