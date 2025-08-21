package com.weather.weather_backend.controller;

import com.weather.weather_backend.service.RealtimeSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class RealtimeController {

    private final RealtimeSseService sseService;

    /**
     * Clients call this to subscribe to a location's live updates.
     * Ex: GET /api/locations/1/stream
     */
    @GetMapping("/{locationId}/stream")
    public SseEmitter stream(@PathVariable Long locationId){
        return sseService.subscribe(locationId);
    }
}
