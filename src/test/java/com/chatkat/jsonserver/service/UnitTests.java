package com.chatkat.jsonserver.service;

import com.chatkat.jsonserver.dataobjects.Channel;
import com.chatkat.jsonserver.dataobjects.Guild;
import com.chatkat.jsonserver.dataobjects.User;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
abstract class UnitTests {
}

class TestUtils {
    public static Channel mockChannel(){
        Channel channel = Channel.builder().id(1).guild_id(1).name("TestChannel").build();
        return channel;
    }
    public static Guild mockGuild(){
        Guild guild = Guild.builder().id(1).name("TestGuild").build();
        return guild;
    }
    public static List<User> mockUsers(){
        List<User> users = new ArrayList<User>();
        User user = new User();
        user.setNick("TestUser1");
        user.setUsername("TestUser1");
        user.setId(1);
        users.add(user);

        User user2 = new User();
        user2.setNick("TestUser2");
        user2.setUsername("TestUser2");
        user2.setId(2);
        users.add(user2);

        User user3 = new User();
        user3.setNick("TestUser3");
        user3.setUsername("TestUser3");
        user3.setId(3);
        users.add(user3);

        return users;
    }

    public static HashMap<Long, Integer> mockScores(){
        return new HashMap<>() {{
            put((long) 1, 13);
            put((long) 2, 11);
            put((long) 3, 9);
        }};
    }
}

class ChannelServiceTests extends UnitTests {
    private Logger log = LoggerFactory.getLogger(ChannelServiceTests.class);
    @MockBean
    InfluxDBService influxDBService;
    @MockBean
    DiscordApiWebClientService discordApiWebClientService;
    @Autowired
    ChannelService channelService;

    void setStandardMocks() {
        Mockito.when(discordApiWebClientService.getChannelById(Mockito.anyLong()))
                .thenReturn(TestUtils.mockChannel());
        Mockito.when(influxDBService.getUserMessageCountsMap(Mockito.any(), Mockito.anyLong()))
                .thenReturn(TestUtils.mockScores());
        Mockito.when(discordApiWebClientService.getUsersByGuildId(Mockito.anyLong()))
                .thenReturn(TestUtils.mockUsers());
    }

    @Test
    void testGetByIdReturnsChannel() {
        setStandardMocks();

        Channel test = channelService.getById(1);
        assertAll("channel",
                () -> assertEquals(test.getName(), "TestChannel"),
                () -> assertEquals(test.getId(), 1),
                () -> assertEquals(test.getGuild_id(), 1),
                () -> assertNotNull(test.getUsers()));
    }

    @Test
    void testUsersAndSums() {
        setStandardMocks();
        Channel test = channelService.getById(1);
        assertAll("channel",
                () -> assertEquals(test.getUsers().get(0).getNick(), "TestUser1"),
                () -> assertEquals(test.getUsers().get(0).getUsername(), "TestUser1"),
                () -> assertEquals(test.getUsers().get(0).getId(), 1),
                () -> assertEquals(test.getUsers().get(0).getSum(), 13)
        );
    }

    @Test
    void testUserOrderBySum() {
        setStandardMocks();
        Channel test = channelService.getById(1);
        assertAll("User1",
                () -> assertEquals(test.getUsers().get(0).getNick(), "TestUser1"),
                () -> assertEquals(test.getUsers().get(0).getUsername(), "TestUser1"),
                () -> assertEquals(test.getUsers().get(0).getId(), 1),
                () -> assertEquals(test.getUsers().get(0).getSum(), 13));
        assertAll("User2",
                () -> assertEquals(test.getUsers().get(1).getNick(), "TestUser2"),
                () -> assertEquals(test.getUsers().get(1).getUsername(), "TestUser2"),
                () -> assertEquals(test.getUsers().get(1).getId(), 2),
                () -> assertEquals(test.getUsers().get(1).getSum(), 11));
        assertAll("User3",
                () -> assertEquals(test.getUsers().get(2).getNick(), "TestUser3"),
                () -> assertEquals(test.getUsers().get(2).getUsername(), "TestUser3"),
                () -> assertEquals(test.getUsers().get(2).getId(), 3),
                () -> assertEquals(test.getUsers().get(2).getSum(), 9)
        );
    }

    @Test
    void testAdditionalUserInRightPlace() {
        setStandardMocks();

        Map<Long, Integer> dbMap = TestUtils.mockScores();
        dbMap.put((long) 4, 14);

        Mockito.when(influxDBService.getUserMessageCountsMap(Mockito.any(), Mockito.anyLong()))
                .thenReturn(dbMap);

        User user4 = new User();
        user4.setNick("TestUser4");
        user4.setUsername("TestUser4");
        user4.setId(4);

        List<User> users = TestUtils.mockUsers();
        users.add(user4);

        Mockito.when(discordApiWebClientService.getUsersByGuildId(Mockito.anyLong()))
                .thenReturn(users);

        Channel test = channelService.getById(1);
        assertAll("channel",
                () -> assertEquals(test.getUsers().size(), 4),
                () -> assertEquals(test.getUsers().get(0), user4)
        );
    }
}

    class GuildServiceTests extends UnitTests {
        private Logger log = LoggerFactory.getLogger(GuildServiceTests.class);
        @MockBean
        InfluxDBService influxDBService;
        @MockBean
        DiscordApiWebClientService discordApiWebClientService;
        @Autowired
        GuildService guildService;

        void setStandardMocks() {
            Mockito.when(discordApiWebClientService.getGuildById(Mockito.anyLong()))
                    .thenReturn(TestUtils.mockGuild());
            Mockito.when(influxDBService.getUserMessageCountsMap(Mockito.any(), Mockito.anyLong()))
                    .thenReturn(TestUtils.mockScores());
            Mockito.when(discordApiWebClientService.getUsersByGuildId(Mockito.anyLong()))
                    .thenReturn(TestUtils.mockUsers());
        }

        @Test
        void testGetByIdReturnsGuild() {
            setStandardMocks();

            Guild test = guildService.getById(1);
            assertAll("guild",
                    () -> assertEquals(test.getName(), "TestGuild"),
                    () -> assertEquals(test.getId(), 1),
                    () -> assertNotNull(test.getUsers()));
        }

        @Test
        void testUsersAndSums() {
            setStandardMocks();
            Guild test = guildService.getById(1);
            assertAll("channel",
                    () -> assertEquals(test.getUsers().get(0).getNick(), "TestUser1"),
                    () -> assertEquals(test.getUsers().get(0).getUsername(), "TestUser1"),
                    () -> assertEquals(test.getUsers().get(0).getId(), 1),
                    () -> assertEquals(test.getUsers().get(0).getSum(), 13)
            );
        }

        @Test
        void testUserOrderBySum() {
            setStandardMocks();
            Guild test = guildService.getById(1);
            assertAll("User1",
                    () -> assertEquals(test.getUsers().get(0).getNick(), "TestUser1"),
                    () -> assertEquals(test.getUsers().get(0).getUsername(), "TestUser1"),
                    () -> assertEquals(test.getUsers().get(0).getId(), 1),
                    () -> assertEquals(test.getUsers().get(0).getSum(), 13));
            assertAll("User2",
                    () -> assertEquals(test.getUsers().get(1).getNick(), "TestUser2"),
                    () -> assertEquals(test.getUsers().get(1).getUsername(), "TestUser2"),
                    () -> assertEquals(test.getUsers().get(1).getId(), 2),
                    () -> assertEquals(test.getUsers().get(1).getSum(), 11));
            assertAll("User3",
                    () -> assertEquals(test.getUsers().get(2).getNick(), "TestUser3"),
                    () -> assertEquals(test.getUsers().get(2).getUsername(), "TestUser3"),
                    () -> assertEquals(test.getUsers().get(2).getId(), 3),
                    () -> assertEquals(test.getUsers().get(2).getSum(), 9)
            );
        }

        @Test
        void testAdditionalUserInRightPlace() {
            setStandardMocks();

            Map<Long, Integer> dbMap = TestUtils.mockScores();
            dbMap.put((long) 4, 14);

            Mockito.when(influxDBService.getUserMessageCountsMap(Mockito.any(), Mockito.anyLong()))
                    .thenReturn(dbMap);

            User user4 = new User();
            user4.setNick("TestUser4");
            user4.setUsername("TestUser4");
            user4.setId(4);

            List<User> users = TestUtils.mockUsers();
            users.add(user4);

            Mockito.when(discordApiWebClientService.getUsersByGuildId(Mockito.anyLong()))
                    .thenReturn(users);

            Guild test = guildService.getById(1);
            assertAll("channel",
                    () -> assertEquals(test.getUsers().size(), 4),
                    () -> assertEquals(test.getUsers().get(0), user4)
            );

        }
}
