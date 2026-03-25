# Fase de build
FROM gradle:9.4.1-jdk25 AS gradle

WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY src/ src/

RUN chmod +x gradlew

RUN ./gradlew build --no-daemon -x test

# Fase de ejecución
FROM eclipse-temurin:25-jdk-alpine-3.21
WORKDIR /app
COPY --from=gradle /app/build/libs /app/build/libs
CMD ["java", "-jar", "/app/build/libs/flexilearn-0.0.1-SNAPSHOT.jar"]