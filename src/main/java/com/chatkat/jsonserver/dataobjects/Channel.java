package com.chatkat.jsonserver.dataobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;


import java.util.Date;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {
    private String name;
    private long id;
    private long guild_id;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date messages_recorded_since;
    @Singular
    private List<User> users;
}
