# --- Build stage ---
FROM maven:3.9.9-amazoncorretto-21-al2023 AS build
WORKDIR /ads
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- Run stage ---
FROM amazoncorretto:21.0.7-al2023
WORKDIR /ads
COPY --from=build /ads/target/*.jar ads.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ads.jar"]