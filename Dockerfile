FROM maven:3.8.6-openjdk-21-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:21-slim AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Exposer le port sur lequel notre application va tourner
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
