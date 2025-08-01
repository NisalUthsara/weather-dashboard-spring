# --- Stage 1: The Builder ---
# Use a full JDK to build the application
FROM azul/zulu-openjdk-alpine:17.0.14 AS builder
WORKDIR /app

# Copy only the necessary files to leverage Docker's cache
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml ./

# Download dependencies first to create a separate cache layer
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Package the application
RUN ./mvnw package -DskipTests

# --- Stage 2: The Final Image ---
# Use a smaller JRE image since we only need to run the app
FROM azul/zulu-openjdk-alpine:17-jre
WORKDIR /app

# Copy only the built JAR from the 'builder' stage
COPY --from=builder /app/target/weather-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run
ENTRYPOINT ["java","-jar","app.jar"]
