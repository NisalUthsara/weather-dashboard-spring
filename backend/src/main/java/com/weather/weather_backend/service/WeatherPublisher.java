package com.weather.weather_backend.service;

import com.weather.weather_backend.dto.WeatherEvent;
import com.weather.weather_backend.model.Location;
import com.weather.weather_backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
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

    private final WebClient openWeatherClient;
    private final LocationRepository locationRepository;
    private final KafkaTemplate<String, WeatherEvent> kafkaTemplate;

    @Value("${WEATHER_API_KEY}")
    private String apiKey;

    @Scheduled(fixedRateString = "${weather.poll.rate:50000}")
    public void publish(){
        List<Location> locations = locationRepository.findAll();
        locations.forEach(this::fetchAndSend);
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
                System.err.println("OpenWeather returned null for " + location.getCityName());
                return;
            }

            WeatherEvent event = WeatherEvent.builder()
                    .locationId(location.getId())
                    .timestamp(Instant.ofEpochSecond(resp.dt()))
                    .temperature(resp.main.temp())
                    .humidity(resp.main().humidity())
                    .windSpeed(resp.wind().speed())
                    .condition(resp.weather().get(0).description())
                    .build();

            kafkaTemplate.send("weather.current", event);
            System.out.println("Published event for " + location.getCityName() + " -> " + event);

        } catch (Exception e) {
            // Do not let one failure stop the entire scheduler
            System.err.println("Error fetching weather for " + location.getCityName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // lightweight records to map only needed JSON fields (can be moved to separate classes)
    private static record OpenWeatherResponse(long dt, Main main, java.util.List<Weather> weather, Wind wind) {}
    private static record Main(double temp, double humidity) {}
    private static record Weather(String description) {}
    private static record Wind(double speed) {}
}
