package com.weather.weather_backend.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.weather_backend.dto.WeatherEvent;
import com.weather.weather_backend.model.Location;
import com.weather.weather_backend.model.WeatherObservation;
import com.weather.weather_backend.repository.LocationRepository;
import com.weather.weather_backend.repository.WeatherObservationRepository;
import com.weather.weather_backend.service.RealtimeSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class WeatherConsumer {

    private final ObjectMapper objectMapper;
    private final WeatherObservationRepository observationRepo;
    private final LocationRepository locationRepo;
    private final RealtimeSseService sseService;

    /**
     * Listens to the 'weather.current' topic, persists each reading, and pushes to SSE subscribers.
     */
    @KafkaListener(topics = "weather.current", groupId = "weather-persist-group")
    @Transactional
    public void consume(String message){
        try {
            WeatherEvent e = objectMapper.readValue(message, WeatherEvent.class);

            //locate Location entity
            Optional<Location> optLoc = locationRepo.findById(e.getLocationId());
            if (optLoc.isEmpty()){
                //unknown location - log and skip
                System.err.println("Unknown locationId in weather event: " + e.getLocationId());
                return;
            }

            Location location = optLoc.get();

            //idempotency: don't insert duplicates with same locationId + timestamp
            Instant observedAt = e.getTimestamp();
            boolean exists = observationRepo.existsByLocationIdAndObservedAt(location.getId(), observedAt);
            if (exists){
                return;
            }

            WeatherObservation obs = WeatherObservation.builder()
                    .location(location)
                    .observedAt(observedAt)
                    .temperature(e.getTemperature())
                    .humidity(e.getHumidity())
                    .windSpeed(e.getWindSpeed())
                    .pressure(11.11)
                    .conditionText(e.getCondition())
                    .build();

            observationRepo.save(obs);

            //push to any SSE subscribers who are listening for this location
            sseService.pushUpdate(location.getId(), obs);
            System.out.println("Saved observation for " + location.getCityName() + " at " + observedAt);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
