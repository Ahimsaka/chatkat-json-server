package com.chatkat.rest;

import com.chatkat.rest.dataobjects.Guild;
import com.chatkat.rest.service.GuildBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

@Component
@EnableAutoConfiguration
public class AppCommandLineRunner implements CommandLineRunner {
    @Autowired
    Logger log;
    @Autowired
    GuildBuilder guildBuilder;

    @Override
    public void run(String... args) {
        String guildId = "631868491955175540";
        log.info("Running");

        Guild guild = guildBuilder.build(guildId);
        log.info(guild.toString());
    }




}
