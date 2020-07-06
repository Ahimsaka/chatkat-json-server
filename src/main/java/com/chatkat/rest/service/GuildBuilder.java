package com.chatkat.rest.service;

import com.chatkat.rest.dataobjects.Guild;
import org.influxdb.dto.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GuildBuilder {
    @Autowired
    private WebClient webClient;
    @Autowired
    private UserArrayBuilder userArrayBuilder;

    // Build Guild POJO with List of User POJOs
    public Guild build(final long guildId) {
        Guild guild = guildData(guildId);

        Query query = new Query(String.format("SELECT * FROM (SELECT sum(\"isValid\") FROM g%d GROUP BY authorID)", guildId));

        guild.setUsers(userArrayBuilder.findUsers(query, guild.getId(), guild.getApproximate_member_count()));

        return guild;
    }

    public Guild guildData(final long guildId) {
        return webClient.get()
                .uri(String.format("/guilds/%d?with_counts=true", guildId))
                .retrieve()
                .bodyToMono(Guild.class)
                .block();
    }
}
