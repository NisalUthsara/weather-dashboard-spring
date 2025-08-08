package com.weather.weather_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient openWeatherClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.openweathermap.org/data/2.5")
                .build();
    }
}
