package com.weather.weather_backend.controller;

import com.weather.weather_backend.service.ObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class ObservationController {

    private final ObservationService observationService;

    @GetMapping("/{locationId}/current")
    public ResponseEntity<?> getCurrent(@PathVariable Long locationId){
        return observationService.getLatestObservation(locationId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
