package com.weather.weather_backend.controller;

import com.weather.weather_backend.dto.LocationDto;
import com.weather.weather_backend.dto.WeatherObservationDto;
import com.weather.weather_backend.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

   private final LocationService locationService;

//   @GetMapping
//   public ResponseEntity<List<LocationDto>> fetchAll(){
//       List<LocationDto> dtos = locationService.getAllLocations();
//       return ResponseEntity.ok(dtos);
//   }

    @GetMapping("/{id}")
    public ResponseEntity<List<WeatherObservationDto>> getSelectedLocationData(){

    }
}
