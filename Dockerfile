# Étape de build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Active le téléchargement des dépendances
RUN mvn dependency:go-offline -Dfile.encoding=UTF-8

# Copie des sources et compilation
COPY src ./src
RUN mvn package -DskipTests -Dfile.encoding=UTF-8

# Étape d'exécution avec une image plus légère
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Exposer le port sur lequel notre application va tourner
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
