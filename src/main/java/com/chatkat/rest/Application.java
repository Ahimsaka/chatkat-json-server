package com.chatkat.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.moshi.Json;
import org.influxdb.InfluxDB;
import org.influxdb.annotation.Column;
import org.influxdb.impl.InfluxDBMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.function.Supplier;

@SpringBootApplication
public class Application {
    Logger log = LoggerFactory.getLogger("Application.java");
    @Value("${spring.bot-token}")
    private String botToken;

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
    @Bean
    public Logger log(){
        return LoggerFactory.getLogger("Application.java");
    }
    @Bean
    public String botToken(){
        return botToken;
    }
    @Bean
    public InfluxDBMapper influxDBMapper(InfluxDB influxDB){
        return new InfluxDBMapper(influxDB.setDatabase("ChatKat"));
    }
    @Bean
    public String stringReturner(String input){
        return input;
    }
    @Bean public WebClient webClient(WebClient.Builder webClientBuilder){
        return webClientBuilder.baseUrl("https://discord.com/api")
                .defaultHeader("Authorization", botToken).build();
    }
}



