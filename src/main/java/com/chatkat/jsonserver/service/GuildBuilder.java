package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.Guild;
import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Objects;

@Service
// build Guild object with user scores
public class GuildBuilder {
    @Autowired
    private WebClient webClient;
    @Autowired
    private UserArrayBuilder userArrayBuilder;

    public Guild build(final long guildId) throws NullPointerException, WebClientResponseException {
        // build Guild object with metadata from discord API
        Guild guild = Objects.requireNonNull(guildData(guildId));

        // set influxDB Query object to variable
        Query query = new Query(String.format("SELECT * FROM (SELECT sum(\"isValid\") FROM g%d GROUP BY authorID)", guildId));

        // set Guild  user scores
        guild.setUsers(userArrayBuilder.findUsers(query, guild.getId(), guild.getApproximate_member_count()));
        return guild;
    }

    // create Guild object with metadata populated from Discord API
    public Guild guildData(final long guildId) throws WebClientResponseException {
        return webClient.get()
                .uri(String.format("/guilds/%d?with_counts=true", guildId))
                .retrieve()
                .bodyToMono(Guild.class)
                .block();
    }
}
