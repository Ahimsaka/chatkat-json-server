package com.chatkat.rest;

import com.chatkat.rest.service.influxdb.InfluxDBMapperTemp;
import org.influxdb.InfluxDB;
import org.influxdb.impl.InfluxDBMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class Application {
    Logger log = LoggerFactory.getLogger("Application.java");

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Logger log() {
        return LoggerFactory.getLogger("Application.java");
    }

    /* Restore original version if influxDBMapper pull request is accepted
    @Bean
    public InfluxDBMapper influxDBMapper(InfluxDB influxDB) {
        return new InfluxDBMapper(influxDB.setDatabase("ChatKat"));
    }*/

    @Bean
    public InfluxDBMapperTemp influxDBMapper(InfluxDB influxDB) {
        return new InfluxDBMapperTemp(influxDB.setDatabase("ChatKat"));
    }

    @Bean
    public WebClient webClient(final WebClient.Builder webClientBuilder, @Value("${spring.bot-token}") final String botToken) {
        return webClientBuilder.baseUrl("https://discord.com/api")
                .defaultHeader("Authorization", botToken).build();
    }


}



