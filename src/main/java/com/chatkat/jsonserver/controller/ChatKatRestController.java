package com.chatkat.jsonserver.controller;

import com.chatkat.jsonserver.dataobjects.Channel;
import com.chatkat.jsonserver.dataobjects.Guild;
import com.chatkat.jsonserver.dataobjects.Directory;
import com.chatkat.jsonserver.service.ChannelService;
import com.chatkat.jsonserver.service.GuildService;
import com.chatkat.jsonserver.service.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;

@RestController
class ChatKatRestController {
    private Logger log = LoggerFactory.getLogger(ChatKatRestController.class);

    @Autowired
    GuildService guildService;
    @Autowired
    ChannelService channelService;
    @Autowired
    DirectoryService directoryService;

    @GetMapping(value={"/", "/dir", "/directory", "/guild", "/channel", "/guilds","/channels"})
    public Directory directory(){
        return directoryService.build();
    }

    @GetMapping("/guild/{id}")
    public Guild guild(@PathVariable("id") final  long id,
                       @RequestParam(value = "unit", defaultValue="") final String unit,
                       @RequestParam(value = "multiple", defaultValue="1") final int multiple) {
        return guildService.getById(id, setInterval(unit, multiple));
    }

    @GetMapping("/channel/{id}")
    public Channel channel(@PathVariable("id") final long id,
                           @RequestParam(value = "unit", defaultValue="") final String unit,
                           @RequestParam(value = "multiple", defaultValue="1") final int multiple) {
        return channelService.getById(id, setInterval(unit, multiple));
    }

    // set long for time-frame based search
    private long setInterval(final String unit, final int multiple) {
        if (unit.equals("")) return 0;
        /*  create HashMap for parsing time parameters. this is done inside the method to ensure that
         *  ZonedDateTime.now() equates to the moment of the request (or slightly after) */
        HashMap<String, Instant> setInterval = new HashMap<>(4) {{
            put("year", ZonedDateTime.now().minusYears(multiple).toInstant());
            put("month", ZonedDateTime.now().minusMonths(multiple).toInstant());
            put("week", ZonedDateTime.now().minusWeeks(multiple).toInstant());
            put("day", ZonedDateTime.now().minusDays(multiple).toInstant());
        }};
        // check request message for time range parameters
        long interval = 0; // set default interval to Epoch to retrieve full history if no params found.
        if (setInterval.containsKey(unit.toLowerCase()))
            interval = setInterval.get(unit.toLowerCase()).toEpochMilli();
        return interval;
    }
}
