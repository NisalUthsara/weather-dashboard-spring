# Use a lightweight JDK
FROM azul/zulu-openjdk-alpine:17.0.14

WORKDIR /app
COPY pom.xml ./
COPY src ./src

# Package the application
RUN ./mvnw package -DskipTests

# Run
ENTRYPOINT ["java","-jar","target/weather-backend-0.0.1-SNAPSHOT.jar"]
