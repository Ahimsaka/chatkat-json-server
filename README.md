# Chatkat JSON Server

Spring Boot server integrates with the influxDB generated by [the ChatKat Discord Bot](https://github.com/Ahimsaka/ChatKat) to serve results in JSON format. 

## Endpoints:

- /guild/{guildID}

    - Returns JSON object with scores for full server/guild.
    
- /channel/{channelID}

    - Returns JSON object with scores for an individual channel.

- /dir 

    - Returns JSON object listing available guilds and guild IDs, and available channels and channel IDs within each guild.

### Time-frame Parameters:

At both /channel/{channelID} and /guild/{guildID}, the following parameters are available:

- unit - if not provided, results from full channel or guild history are returned - accepts:

    - day
    - week
    - month
    - year
   
- multiple - accepts any positive integer. If unit is not provided, multiple has no effect on results. 

Examples:

- /channel/{channelID}?unit=day&multiple=3
    
    - returns results from the last three days.
   
- /guild/{guildID}?unit=week&multiple=4

    - returns results from the last four weeks. 

## Configuring Discord Bot: 
- create or open file: src/main/java/resources/application.properties
- set spring.bot-token=Bot {YOUR BOT TOKEN}

### Configuring InfluxDB Server
- create or open file: src/main/java/resources/application.properties
- set:

    - spring.influx.url={url:port}
    - spring.influx.user={influxdb username}
    - spring.influx.password={influxdb password} 