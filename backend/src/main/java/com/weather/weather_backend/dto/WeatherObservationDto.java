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
public class WeatherObservationDto {
    private Long id;
    private Long locationId;
    private Instant observedAt;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private Double pressure;
    private String conditionText;
}
