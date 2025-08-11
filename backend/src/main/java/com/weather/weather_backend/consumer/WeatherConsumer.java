package com.weather.weather_backend.consumer;

import com.weather.weather_backend.dto.WeatherEvent;
import com.weather.weather_backend.model.Location;
import com.weather.weather_backend.model.WeatherObservation;
import com.weather.weather_backend.repository.LocationRepository;
import com.weather.weather_backend.repository.WeatherObservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherConsumer {

    private final WeatherObservationRepository weatherObservationRepository;
    private final LocationRepository locationRepo;

    @KafkaListener(topics = "weather.current", groupId = "weather-consumers")
    public void handle(WeatherEvent event){
        try {
            //find location entity (should exist)
            Optional<Location> optLoc = locationRepo.findById(event.getLocationId());

            if (optLoc.isEmpty()){
                System.err.println("Unknown location id " + event.getLocationId());
                return;
            }

            Location loc = optLoc.get();
            Instant observedAt = event.getTimestamp();

            boolean exists = weatherObservationRepository.existsByLocationIdAndObservedAt(loc.getId(), observedAt);
            if (exists){
                System.out.println("Duplicate observation for " + loc.getCityName() + " at "+ observedAt );
                return;
            }

            WeatherObservation obs = WeatherObservation.builder()
                    .location(loc)
                    .observedAt(observedAt)
                    .temperature(event.getTemperature())
                    .humidity(event.getHumidity())
                    .windSpeed(event.getWindSpeed())
                    .pressure(11.11)
                    .conditionText(event.getCondition())
                    .build();

            weatherObservationRepository.save(obs);
            System.out.println("Saved observation for " + loc.getCityName() + " at " + observedAt );

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
