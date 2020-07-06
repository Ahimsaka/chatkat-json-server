Configuring Discord Bot: 
- create or open file: src/main/java/resources/application.properties
- set spring.bot-token=Bot {YOUR BOT TOKEN}

Configuring InfluxDB Server
- create or open file: src/main/java/resources/application.properties
- set:

    - spring.influx.url={url:port}
    - spring.influx.user={influxdb username}
    - spring.influx.password={influxdb password} 