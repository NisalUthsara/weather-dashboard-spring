package com.weather.weather_backend.service;

import com.weather.weather_backend.dto.ObservationDto;
import com.weather.weather_backend.model.WeatherObservation;
import com.weather.weather_backend.repository.WeatherObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ObservationService {

    private final WeatherObservationRepository observationRepository;

    /**
     * Returns the latest observation for the location as DTO if present.
     */
    public Optional<ObservationDto> getLatestObservation (Long locationId) {
        Optional<WeatherObservation> opt = observationRepository.findTopByLocationInOrderByObservedAtDesc(locationId);
        return opt.map(this::toDto);
    }

    private ObservationDto toDto(WeatherObservation entity) {
        Instant now = Instant.now();
        long ageSeconds = Duration.between(entity.getObservedAt(), now).getSeconds();
        return ObservationDto.builder()
                .locationId(entity.getLocation().getId())
                .observedAt(entity.getObservedAt())
                .temperature(entity.getTemperature())
                .humidity(entity.getHumidity())
                .pressure(entity.getPressure())
                .conditionText(entity.getConditionText())
                .ageSeconds(ageSeconds)
                .build();
    }
}
