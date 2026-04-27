# Stage 1: Build
FROM maven:3.8.4-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar app.jar

# IMPORTANT: These lines link Render's variables to Spring Boot
ENV SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
ENV SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
ENV SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]