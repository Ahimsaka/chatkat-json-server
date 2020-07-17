package com.chatkat.jsonserver.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.influxdb.annotation.Column;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private long id;
    private String username;
    private String nick;
    private int sum;

    @JsonProperty("user")
    private void unpack(Map<String, Object> user) {
        setId(Long.parseLong((String) user.get("id")));
        setUsername((String) user.get("username"));
    }
}
