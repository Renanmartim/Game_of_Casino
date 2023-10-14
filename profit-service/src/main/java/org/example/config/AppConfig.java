package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

@Configuration
public class AppConfig {

    @Bean
    public LocalTime localTime() {
        return LocalTime.now();
    }
}