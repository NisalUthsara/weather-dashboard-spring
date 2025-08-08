package com.weather.weather_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherEvent {
    private Long locationId;
    private Instant timestamp;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private String condition;
}
