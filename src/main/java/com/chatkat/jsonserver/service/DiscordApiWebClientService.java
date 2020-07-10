package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.Channel;
import com.chatkat.jsonserver.dataobjects.Guild;
import com.chatkat.jsonserver.dataobjects.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class DiscordApiWebClientService {
    private Logger log = LoggerFactory.getLogger(DiscordApiWebClientService.class);
    @Autowired
    WebClient webClient;

    // create Channel object with populated metadata from Discord API.
    public Channel getChannelById(final long channelId) throws WebClientResponseException {
        return webClient.get()
                .uri(String.format("/channels/%d", channelId))
                .retrieve()
                .bodyToMono(Channel.class)
                .block();
    }

    // create Guild object with metadata populated from Discord API
    public Guild getGuildById(final long guildId) throws WebClientResponseException {
        return webClient.get()
                .uri(String.format("/guilds/%d?with_counts=true", guildId))
                .retrieve()
                .bodyToMono(Guild.class)
                .block();
    }

    // get list of User objects with metadata populated from Discord API
    public List<User> getUsersByGuildId(final long guildId) {
        /* Discord API limits member list requests to a maximum of 1000 records per call.
        *  In case of guilds with over 1000 members, store the userId of the last from each
        * call for use in "last" param on subsequent requests, and initialize empty arrayList
        * to aggregate calls. */
        long lastUserId = 0;
        List<User> usersFound = new ArrayList<>();

        while (true) {
            List<User> usersFoundThisLoop = Objects.requireNonNull(webClient.get()
                    // default value for limit parameter is 1, so must be set.
                    .uri(String.format("/guilds/%s/members?limit=1000&after=%d", guildId, lastUserId))
                    .retrieve()
                    // retrieve results as a flux of User objects
                    .bodyToMono(User[].class)
                    .map(Arrays::asList)
                    .block());
            usersFound.addAll(usersFoundThisLoop);
            lastUserId = usersFound.get(usersFound.size()-1).getId();

            if (usersFoundThisLoop.size() < 1000) break;
        }
        return usersFound;
    }
}
