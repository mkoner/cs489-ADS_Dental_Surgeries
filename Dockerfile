<<<<<<< HEAD
# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /ads
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /ads
COPY --from=build /ads/target/*.jar ads.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ads.jar"]
=======
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
>>>>>>> ab04d687b914c7d32a8f9a4aed3eeb54d20eed42
