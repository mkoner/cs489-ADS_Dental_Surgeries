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
