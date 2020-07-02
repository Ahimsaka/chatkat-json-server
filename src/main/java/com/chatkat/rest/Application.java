package com.chatkat.rest;

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
    @Value("${spring.bot-token}")
    private String botToken;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Logger log() {
        return LoggerFactory.getLogger("Application.java");
    }

    @Bean
    public String botToken() {
        return botToken;
    }

    @Bean
    public InfluxDBMapper influxDBMapper(InfluxDB influxDB) {
        return new InfluxDBMapper(influxDB.setDatabase("ChatKat"));
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("https://discord.com/api")
                .defaultHeader("Authorization", botToken).build();
    }



}



