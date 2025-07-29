CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    city_name TEXT NOT NULL,
    country_code VARCHAR(3) NOT NULL
);

CREATE TABLE weather_observations (
    id SERIAL PRIMARY KEY,
    location_id INTEGER NOT NULL REFERENCES locations(id),
    observed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    temperature DOUBLE PRECISION NOT NULL,
    humidity DOUBLE PRECISION NOT NULL,
    wind_speed DOUBLE PRECISION NOT NULL,
    pressure DOUBLE PRECISION NOT NULL,
    condition_text TEXT NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    role TEXT NOT NULL,
    is_active BOOLEAN NOT NULL
);

CREATE TABLE user_favorites (
    user_id INTEGER NOT NULL REFERENCES users(id),
    location_id INTEGER NOT NULL REFERENCES locations(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (user_id, location_id)
);
