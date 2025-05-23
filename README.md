# Easy Rental Car API

## 📋 Description

Easy Rental Car API est une application Spring Boot développée selon les principes de la **Clean Architecture**. Elle fournit une API REST robuste pour la gestion de location de véhicules avec authentification externe et stockage distribué.

### Caractéristiques principales

- ✅ **Architecture Clean** - Séparation claire des responsabilités
- ✅ **Authentification externe** - Intégration avec un service d'authentification tiers
- ✅ **Base de données NoSQL** - Utilisation de Cassandra/ScyllaDB
- ✅ **Design Patterns** - Implementation des patterns Builder et Factory
- ✅ **Documentation API** - Swagger/OpenAPI intégré
- ✅ **Validation** - Validation des données d'entrée avec Bean Validation
- ✅ **Gestion d'erreurs** - Gestion globale des exceptions
- ✅ **Containerisation** - Docker et Docker Compose prêts

## 🏗️ Architecture

Le projet suit l'architecture en couches (Clean Architecture) :

```
├── 📁 domain/              # Cœur métier - Entités et règles business
│   ├── model/              # Modèles du domaine
│   ├── repository/         # Interfaces des repositories
│   ├── service/            # Interfaces des services métier
│   └── exception/          # Exceptions métier
├── 📁 application/         # Couche application - Use cases
│   ├── service/            # Implémentations des services
│   ├── usecase/            # Cas d'usage métier
│   ├── dto/                # DTOs de la couche application
│   └── mapper/             # Mappers application
├── 📁 infrastructure/      # Couche infrastructure - Implémentations techniques
│   ├── persistence/        # Persistance des données (Cassandra)
│   ├── external/           # Services externes (Auth, Notifications, Payments)
│   └── config/             # Configuration Spring
└── 📁 presentation/        # Couche présentation - API REST
    ├── controller/         # Contrôleurs REST
    ├── dto/                # DTOs de l'API
    ├── mapper/             # Mappers de présentation
    └── exception/          # Gestion des erreurs HTTP
```

## 🚀 Technologies utilisées

### Backend
- **Java 21** - Langage de programmation
- **Spring Boot 3.2.5** - Framework principal
- **Spring Data Cassandra** - Accès aux données
- **Spring WebFlux** - Client HTTP réactif
- **Spring Validation** - Validation des données

### Base de données
- **ScyllaDB** - Base de données NoSQL haute performance

### Documentation & Tests
- **SpringDoc OpenAPI** - Documentation automatique de l'API
- **JUnit 5** - Tests unitaires

### DevOps
- **Docker & Docker Compose** - Containerisation
- **Maven** - Gestion des dépendances

## 📦 Installation et démarrage

### Prérequis

- Java 21+
- Docker et Docker Compose
- Maven 3.6+

### 1. Cloner le repository

```bash
git clone <repository-url>
cd rentalcar
```

### 2. Démarrage avec Docker Compose

```bash
# Construire et démarrer tous les services
docker-compose up --build

# Ou en arrière-plan
docker-compose up -d --build
```

L'application sera accessible sur :
- **API** : http://localhost:8080
- **Documentation Swagger** : http://localhost:8080/swagger-ui.html
- **ScyllaDB** : localhost:9045

### 3. Démarrage en développement

```bash
# Démarrer uniquement la base de données
docker-compose up scylla -d

# Démarrer l'application en mode développement
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📚 Utilisation de l'API

### Endpoints principaux

#### Authentification
- `POST /api/auth/register` - Inscription d'un nouvel utilisateur
- `POST /api/auth/login` - Connexion utilisateur
- `POST /api/proxy/auth/register` - Proxy d'inscription (évite CORS)
- `POST /api/proxy/auth/login` - Proxy de connexion (évite CORS)

#### Exemple de requête d'inscription

```json
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john.doe@example.com",
  "name": "John Doe",
  "phoneNumber": "+237675473829",
  "password": "password123",
  "role": "customer"
}
```

#### Exemple de réponse

```json
{
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "username": "johndoe",
    "email": "john.doe@example.com",
    "name": "John Doe",
    "phoneNumber": "+237675473829",
    "role": "customer",
    "active": true,
    "createdAt": "2024-05-23T10:30:00"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "success": true,
  "message": "Utilisateur enregistré avec succès"
}
```

### Documentation interactive

Accédez à la documentation Swagger : http://localhost:8080/swagger-ui.html

## ⚙️ Configuration

### Variables d'environnement

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `SPRING_PROFILES_ACTIVE` | Profil Spring actif | `dev` |
| `SPRING_DATA_CASSANDRA_CONTACT_POINTS` | Adresse Cassandra | `localhost` |
| `SPRING_DATA_CASSANDRA_PORT` | Port Cassandra | `9045` |
| `SPRING_DATA_CASSANDRA_KEYSPACE_NAME` | Keyspace Cassandra | `rental` |
| `external.api.url` | URL de l'API externe | `https://gateway.yowyob.com` |

### Profils disponibles

- **dev** - Développement local avec ScyllaDB
- **prod** - Production avec configuration optimisée

## 🏛️ Patterns implémentés

### Pattern Builder
Utilisé pour la construction flexible des DTOs :

```java
UserResponse user = UserResponse.builder()
    .id(UUID.randomUUID())
    .username("johndoe")
    .email("john@example.com")
    .build();
```

### Pattern Factory
Utilisé pour créer les requêtes vers les services externes :

```java
@Component
public class ExternalAuthRequestFactory {
    public ExternalAuthRegisterRequest createRegisterRequest(User user) {
        return ExternalAuthRegisterRequest.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .password(user.getPassword())
            .build();
    }
}
```

## 🔧 Services externes

L'application s'intègre avec plusieurs services externes :

### Service d'authentification
- **URL** : `https://gateway.yowyob.com/auth-service`
- **Fonctionnalités** : Inscription, connexion, validation de token
- **Communication** : WebClient (réactif)

### Services de notification (à implémenter)
- Email, SMS, Push notifications

### Services de paiement (à implémenter)
- Intégration avec providers de paiement

## 🧪 Tests

```bash
# Exécuter tous les tests
./mvnw test

# Tests avec couverture
./mvnw test jacoco:report
```

## 📊 Monitoring et logs

L'application utilise les loggers Spring Boot standards. Les logs sont configurés pour différents niveaux selon l'environnement.

## 🚢 Déploiement

### Build de l'image Docker

```bash
docker build -t easy-rental-car:latest .
```

### Variables d'environnement de production

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATA_CASSANDRA_CONTACT_POINTS=production-cassandra-host
export SPRING_DATA_CASSANDRA_USERNAME=cassandra_user
export SPRING_DATA_CASSANDRA_PASSWORD=secure_password
```

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

### Standards de code

- Utiliser Lombok pour réduire le boilerplate
- Suivre les conventions de nommage Java
- Documenter les méthodes publiques
- Écrire des tests pour les nouvelles fonctionnalités

## 📝 Changelog

### Version 0.0.1-SNAPSHOT
- ✅ Implémentation de l'architecture Clean
- ✅ Système d'authentification avec service externe
- ✅ Intégration Cassandra/ScyllaDB
- ✅ Documentation Swagger
- ✅ Containerisation Docker

## 📄 License

Ce projet est propriétaire de **Yowyob Inc.**

## 👥 Équipe

- **Développement** : Équipe Yowyob
- **Architecture** : Clean Architecture avec Spring Boot
- **Contact** : [contact@yowyob.com](mailto:contact@yowyob.com)

---

## 🔗 Liens utiles

- [Documentation Spring Boot](https://spring.io/projects/spring-boot)
- [ScyllaDB Documentation](https://docs.scylladb.com/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [API Documentation](http://localhost:8080/swagger-ui.html) (quand l'app est démarrée)

---

**Made with ❤️ by Yowyob Inc.**
