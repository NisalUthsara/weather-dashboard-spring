package com.weather.weather_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.weather_backend.dto.WeatherEvent;
import com.weather.weather_backend.model.Location;
import com.weather.weather_backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherPublisher {
    private static final Logger log = LoggerFactory.getLogger(WeatherPublisher.class);

    private final WebClient openWeatherClient;
    private final LocationRepository locationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value("${WEATHER_API_KEY}")
    private String apiKey;

    @Scheduled(fixedRateString = "${weather.poll.rate:60000}")
    public void publish(){
        try {
            List<Location> locations = locationRepository.findAll();
            if (locations.isEmpty()){
                log.debug("No locations found to poll.");
                return;
            }
            log.debug("Polling {} locations for current weather...", locations.size());
            locations.forEach(this::fetchAndSend);
        }catch (Exception e){
            log.error("Unexpected error in publishAllLocations", e);
        }
    }

    private void fetchAndSend(Location location) {
        try {
            // Build and execute the request synchronously (block())
            OpenWeatherResponse resp = openWeatherClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("lat", location.getLatitude())
                            .queryParam("lon", location.getLongitude())
                            .queryParam("units", "metric")
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(OpenWeatherResponse.class)
                    .block();   // blocking call - simple and easy to reason about

            if (resp == null) {
                log.warn("OpenWeather returned null for {} (id={})",
                        location.getCityName(), location.getId());
                return;
            }

            //null safe extraction
            Double temp = resp.main() != null ? resp.main().temp() : null;
            Double humidity = resp.main() != null ? resp.main().humidity() : null;
            Double windSpeed = resp.wind() != null ? resp.wind().speed() : null;
            String condition = (resp.weather() != null && !resp.weather().isEmpty())
                    ? resp.weather().get(0).description()
                    : "unknown";

            WeatherEvent event = WeatherEvent.builder()
                    .locationId(location.getId())
                    .timestamp(Instant.ofEpochSecond(resp.dt()))
                    .temperature(temp)
                    .humidity(humidity)
                    .windSpeed(windSpeed)
                    .condition(condition)
                    .build();

            //Serialize to JSON string
            String json = objectMapper.writeValueAsString(event);

            kafkaTemplate.send("weather.current", String.valueOf(location.getId()), json);

            log.info("Published event for {} -> {}", location.getCityName(), event);

        } catch (Exception e) {
            log.error("Error fetching weather for {} (id={}): {}",
                    location.getCityName(), location.getId(), e.getMessage(), e);
        }
    }

    // lightweight records to map only needed JSON fields (can be moved to separate classes)
    private static record OpenWeatherResponse(long dt, Main main, java.util.List<Weather> weather, Wind wind) {}
    private static record Main(double temp, double humidity) {}
    private static record Weather(String description) {}
    private static record Wind(double speed) {}
}
