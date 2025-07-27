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
public class UserFavoriteDto {
    private Long userId;
    private Long locationId;
    private Instant createdAt;
}
