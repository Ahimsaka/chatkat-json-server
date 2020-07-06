package com.chatkat.rest;

import com.chatkat.rest.service.ChannelBuilder;
import com.chatkat.rest.service.GuildBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@EnableAutoConfiguration
public class AppCommandLineRunner implements CommandLineRunner {
    @Autowired
    Logger log;
    @Autowired
    GuildBuilder guildBuilder;
    @Autowired
    ChannelBuilder channelBuilder;
    @Autowired
    WebClient webClient;

    @Override
    public void run(String... args) {
        log.info("Running");

        String channelId = "631868491959369730";


        //Channel channel = channelBuilder.build(channelId);
        //log.info(channel.toString());

        /*String guildId = "631868491955175540";
        Guild guild = guildBuilder.build(guildId);
        log.info(guild.toString());
        Channel channel = channelBuilder.build(channelId);
        log.info(channel.toString());*/
    }




}
