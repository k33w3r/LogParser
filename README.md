# LogParser

A **Spring Boot-based log analysis service** that parses system logs, tracks user actions, detects anomalous logins, and exposes analytical results through a RESTful API.

---

## Features

- Parse system/application logs into structured data
- Identify top users and detect suspicious login patterns
- Generate and export results as JSON
- REST API endpoints for analytics
- Containerized deployment via Docker/Podman
- Automated CI/CD pipeline with GitHub Actions
- Swagger UI documentation for all endpoints

---

## Tech Stack

| Component | Description |
|-----------|--------------|
| **Java** | Core application written using Java 17 |
| **Spring Boot** | REST API and service orchestration |
| **Log4j2 / SLF4J** | Logging and monitoring |
| **JUnit** | Unit testing framework |
| **Maven** | Dependency and build management |
| **Podman / Docker** | Containerization and local deployment |
| **Render** | Hosting for the live demo environment |

---

## Quick Start

### 1. Clone the Repository
```

git clone https://github.com/k33w3r/LogParser.git
cd LogParser

```

### 2. Run with Podman or Docker
From the project root `/docker` directory:

```

podman pull docker.io/k33w3r/logparser:latest
podman-compose up

```

Once running, the application can be accessed at:

```

http://localhost:8080/log-parser/swagger-ui/index.html

```

### 3. Run Locally via Maven
For developers who want to contribute or debug:

```

mvn clean install
mvn spring-boot:run

```

---

## API Overview

The application exposes several endpoints for log operations and reporting.


Full Swagger documentation is available at:  
[https://logparser.onrender.com/log-parser/swagger-ui/index.html](https://logparser.onrender.com/log-parser/swagger-ui/index.html)

> Note: The public instance might take a minute to wake up due to Render’s auto-scaling.

---

## Development Pipeline

This project uses a **GitHub Actions** workflow that ensures code quality and workflow automation.

- Builds and runs unit tests on every PR
- Blocks merges to `main` if CI fails
- Automatically deploys passing builds to **Render**
- Makes Swagger and REST endpoints available online
---

## Testing

To execute the test suite:

```

mvn test

```

Tests validate:
- Log parsing logic accuracy
- REST controller behavior
- Business rule validation (e.g., suspicious login criteria)

You can find test classes under:
```

src/test/java/com/k33w3r/logparser/

```

---

## Task Management

Development tasks were tracked using **GitHub Issues**. 

History can be followed there after I created the build pipeline.
Some issues are still open that I did not get around to.

---