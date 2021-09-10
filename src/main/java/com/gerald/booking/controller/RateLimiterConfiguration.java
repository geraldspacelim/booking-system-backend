package com.gerald.booking.controller;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfiguration {

    // rate limiter allows a maximum to 10 request per second in a 30 seconds period
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(10, Duration.ofSeconds(30));
    }

}
