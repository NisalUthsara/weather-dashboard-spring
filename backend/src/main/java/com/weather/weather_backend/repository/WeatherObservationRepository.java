package com.weather.weather_backend.repository;

import com.weather.weather_backend.model.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long> {
    List<WeatherObservation> findByLocationIdAndObservedAtBetween(
            Long locationId,
            Instant from,
            Instant to
    );
}
