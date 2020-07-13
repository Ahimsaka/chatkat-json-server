package com.chatkat.jsonserver;

import com.chatkat.jsonserver.service.DiscordApiWebClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
/* boilerplate base application class which also defines 3 magic Beans */
public class Application {
    private Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // add baseUrl and default authorization header to Spring Boot configured webclient
    @Bean
    public WebClient webClient(final WebClient.Builder webClientBuilder, @Value("${spring.bot-token}") final String botToken) {
        return webClientBuilder.baseUrl("https://discord.com/api")
                .defaultHeader("Authorization", botToken).build();
    }

    @Component
    public class CommandLineAppStartupRunner implements CommandLineRunner {
        @Autowired
        DiscordApiWebClientService discordApiWebClientService;

        @Override
        public void run(String... args) throws Exception {
            log.info("Server's up, big hoss.");
        }
    }
}





