package com.chatkat.jsonserver;

import com.chatkat.jsonserver.service.InfluxDBMapperTemp;
import org.influxdb.InfluxDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
/* boilerplate base application class which also defines 3 magic Beans */
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Logger log() {
        return LoggerFactory.getLogger("Application.java");
    }

    /* InfluxDBMapperTemp extends InfluxDBMapper to add a signature to
    * InfluxDBMapper.query() that utilizes a relatively new signature of
    * InfluxDBResultMapper.toPojo(), allowing InfluxDBMapper to map a query
    * with variable measurement name. I have made a pull request on influxdb-java
    * suggesting that the additional signature of query() be added to InfluxDBMapper. */
    @Bean
    public InfluxDBMapperTemp influxDBMapper(InfluxDB influxDB) {
        return new InfluxDBMapperTemp(influxDB.setDatabase("ChatKat"));
    }
    /* Restore original version if influxDBMapper pull request is accepted
    @Bean
    public InfluxDBMapper influxDBMapper(InfluxDB influxDB) {
        return new InfluxDBMapper(influxDB.setDatabase("ChatKat"));
    }
    */

    // add baseUrl and default authorization header to Spring Boot configured webclient
    @Bean
    public WebClient webClient(final WebClient.Builder webClientBuilder, @Value("${spring.bot-token}") final String botToken) {
        return webClientBuilder.baseUrl("https://discord.com/api")
                .defaultHeader("Authorization", botToken).build();
    }

}



