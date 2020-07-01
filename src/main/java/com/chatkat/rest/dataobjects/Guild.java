package com.chatkat.rest.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Guild {
    long id;
    String name;
    int approximate_member_count;
    List<UserResult> members;

    @Override
    public String toString(){
        return String.format("Guild: %s, id: %d, members-total: %d, members: %s}", name, id, approximate_member_count, members.toString());
    }

    public void setMembers(WebClient webClient, List<UserResult> dbResults){
        members = new ArrayList<>(dbResults.size());
        for (int i = 0; i < approximate_member_count; i+=1000){
            members.addAll(Objects.requireNonNull(webClient.get()
                    .uri(String.format("https://discord.com/api/guilds/%d/members?limit=1000&after=%d", id, i))
                    .retrieve()
                    .bodyToFlux(UserMeta.class)
                    .flatMap(userMeta -> Flux.fromIterable(dbResults)
                            .filter(userResult -> userResult.compareMeta(userMeta))
                            .map(userResult -> userResult.setMeta(userMeta))
                    )
                    .sort(Comparator.comparingInt(UserResult::getSum).reversed())
                    .collectList().block()));
        }
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setApproximate_member_count(int approximate_member_count) {
        this.approximate_member_count = approximate_member_count;
    }
    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }
    public int getApproximate_member_count() {
        return approximate_member_count;
    }
    public String getName() {
        return name;
    }
    public List<UserResult> getMembers() {
        return members;
    }
}
