# --- Build stage ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /ads
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- Run stage ---
FROM amazoncorretto:21-alpine
WORKDIR /ads
COPY --from=build /ads/target/*.jar ads.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ads.jar"]