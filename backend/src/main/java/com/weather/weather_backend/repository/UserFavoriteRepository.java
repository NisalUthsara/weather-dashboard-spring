package com.weather.weather_backend.repository;

import com.weather.weather_backend.model.UserFavorite;
import com.weather.weather_backend.model.UserFavoriteId;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@ReadingConverter
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, UserFavoriteId> {
    List<UserFavorite> findByUserId(Long userId);
}
