--Insert initial test data into the locations and users tables

-- 1. seed some locations
INSERT INTO locations (latitude, longitude, city_name, country_code) VALUES
    (6.9271, 79.8612, 'Colombo', 'LK'),
    (51.5074, -0.1278, 'London', 'GB'),
    (40.7128, -74.0060, 'New York', 'US');

-- 2. Seed a test user (password is 'password' hashed with BCrypt)
INSERT INTO users (email, password_hash, created_at, role, is_active)
VALUES
    ('test@example.com',
     '$2a$10$DowJXbZteL/YO1uMp2wO8eX0Y3mB46EeZmkM8pX39Sqz8yhbZ4LQu', -- BCrypt('password')
     NOW(),
     'ROLE_USER',
     true);

-- 3. Associate the test user with a favorite location
INSERT INTO user_favorites (user_id, location_id, created_at)
VALUES
    (1, 1, NOW());

-- 4. Optionally seed one weather observation for each location
INSERT INTO weather_observations (location_id, observed_at, temperature, humidity, wind_speed, pressure, condition_text)
VALUES
    (1, NOW() - INTERVAL '1 hour', 30.5, 70.0, 5.2, 1012.3, 'Sunny'),
    (2, NOW() - INTERVAL '1 hour', 20.1, 55.0, 3.5, 1015.0, 'Cloudy'),
    (3, NOW() - INTERVAL '1 hour', 25.0, 60.0, 4.0, 1013.2, 'Clear');