# Java CMS Backend

A lightweight CMS backend built with Spring Boot and DDD (Domain-Driven Design) architecture.

## Tech Stack

- **Java**: 17+
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Maven
- **Architecture**: DDD Layered Architecture
- **Security**: Spring Security
- **Data Storage**: In-Memory (ConcurrentHashMap)

## Project Structure

```
src/main/java/com/java/
├── JavaApplication.java          # Application entry point
├── presentation/                  # Presentation Layer
│   ├── controller/               # REST Controllers
│   └── dto/                      # Data Transfer Objects
├── application/                   # Application Layer
│   └── service/                  # Application Services
├── domain/                        # Domain Layer
│   ├── entity/                   # Domain Entities
│   ├── valueobject/              # Value Objects
│   ├── service/                  # Domain Services
│   └── repository/               # Repository Interfaces
├── infrastructure/                # Infrastructure Layer
│   ├── repository/               # Repository Implementations
│   └── config/                   # Configuration Classes
└── util/                          # Utility Classes
```

## Getting Started

### Prerequisites

- JDK 17+
- Maven 3.8+

### Run the Application

```bash
# Build
mvn clean package

# Run
java -jar target/java-backend-0.0.1-SNAPSHOT.jar

# Or run with Maven
mvn spring-boot:run
```

### Development Mode

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## API Response Format

All API responses follow this format:

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

## License

Internal use only.
