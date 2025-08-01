package com.weather.weather_backend.service;

import com.weather.weather_backend.dto.LocationDto;
import com.weather.weather_backend.model.Location;
import com.weather.weather_backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<LocationDto> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private LocationDto toDto(Location loc){
        return LocationDto.builder()
                .id(loc.getId())
                .cityName(loc.getCityName())
                .countryCode(loc.getCountryCode())
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .build();
    }
}
