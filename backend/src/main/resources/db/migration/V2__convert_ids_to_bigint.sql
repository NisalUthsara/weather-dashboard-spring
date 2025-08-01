-- Purpose: Alter primary key columns from INTEGER (SERIAL) to BIGINT

-- 1. Alter 'locations' table
ALTER TABLE locations
ALTER COLUMN id TYPE BIGINT;

-- 2. Alter sequence backing 'locations.id' if needed (PostgreSQL sequences produce BIGINT by default)
-- Usually no change required for the sequence itself.

-- 3. Alter 'weather_observations' table
ALTER TABLE weather_observations
ALTER COLUMN id TYPE BIGINT;

-- 4. Alter 'users' table
ALTER TABLE users
ALTER COLUMN id TYPE BIGINT;

-- 5. Alter sequence backing 'users.id' if needed (beyond default)

-- Note: For composite PK in 'user_favorites', no id column exists.
-- Foreign key columns reference bigint primary keys, so ensure they match:
ALTER TABLE weather_observations
ALTER COLUMN location_id TYPE BIGINT;

ALTER TABLE user_favorites
ALTER COLUMN user_id TYPE BIGINT,
  ALTER COLUMN location_id TYPE BIGINT;