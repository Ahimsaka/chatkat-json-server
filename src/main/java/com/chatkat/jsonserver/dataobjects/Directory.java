package com.chatkat.jsonserver.dataobjects;

import com.chatkat.jsonserver.dataobjects.Guild;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class Directory {
    List<Guild> guilds;
}
