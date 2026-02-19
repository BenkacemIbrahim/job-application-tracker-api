package com.ibrahim.jobtracker.config;

import java.time.Clock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
public class BaseConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public CommandLineRunner databaseConnectionVerifier(JdbcTemplate jdbcTemplate) {
        return args -> {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.info("Database connectivity check passed. SELECT 1 returned {}", result);
        };
    }
}
