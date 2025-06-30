# 🔐 Authentication Service

A Spring Boot microservice for handling user registration, login, and stateless JWT-based authentication with optional public key (JWKS) exposure for RS256.

---

## 📌 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
  - [Prerequisites](#-prerequisites)
  - [Build & Run](#-build--run)
  - [Configuration](#-configuration)
  - [Running Tests](#-running-tests)
- [API Endpoints](#-api-endpoints)
- [Project Structure](#-project-structure)
- [Author](#-author)
- [Show Your Support](#-show-your-support)

---

## ✅ Features

- User registration & login
- JWT-based authentication (HS256 or RS256)
- Exposes public key via JWKS endpoint (for RS256)
- BCrypt password hashing
- Secure JWT generation and validation
- Optional: refresh tokens & Redis integration
- Layered architecture & global error handling

---

## 🧰 Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Data JPA**
- **JWT (JJWT or Nimbus)**
- **MySQL / H2**
- **Redis (optional)**
- **JUnit 5 + Mockito**
- **Maven**

---

## 🚀 Getting Started

### ✅ Prerequisites

Make sure you have the following installed:

- Java 17+
- Maven 3.8+
- MySQL (or H2 for testing)
- Redis (optional for refresh tokens)

---

### ▶️ Build & Run

#### Clone the repository

```bash
git clone https://github.com/your-username/auth-service.git
cd auth-service
Build the project
bash
Copy
Edit
mvn clean install
Run the application
bash
Copy
Edit
mvn spring-boot:run
⚙️ Configuration
Update application properties:

bash
Copy
Edit
src/main/resources/application.properties
properties
Copy
Edit
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/authdb
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update

# JWT - RS256
jwt.private-key.path=classpath:private.pem
jwt.public-key.path=classpath:public.pem
jwt.expiration=3600000

# Alternatively for HS256
# jwt.secret=your-hs256-secret-key

# Optional Redis config
spring.data.redis.host=localhost
spring.data.redis.port=6379
🧪 Running Tests
bash
Copy
Edit
mvn test
Includes:

Service layer tests

JWT utility tests

Controller-level integration tests

📡 API Endpoints
👤 Auth APIs
Method	Endpoint	Description
POST	/auth/register	Register new user
POST	/auth/login	Login and get JWT

🙍‍♂️ User APIs
Method	Endpoint	Description
GET	/users/me	Get current user info

🔐 Public Key (RS256)
Method	Endpoint	Description
GET	/.well-known/jwks.json	Public key in JWKS format

🗃️ Project Structure
plaintext
Copy
Edit
src/
├── configs/             # JWT, Security, CORS config
├── controllers/         # Auth & User endpoints
├── dtos/                # DTOs for requests/responses
├── exceptions/          # Global error handlers
├── jwks/                # JWKS endpoint support
├── models/              # JPA entities like User, Role
├── repositories/        # UserRepository
├── service/             # UserService, JwtService
└── AuthenticationServiceApplication.java
🙋‍♂️ Author
Shubham Gupta

GitHub: ShubhamGupta78

LinkedIn: Shubham Gupta

🌟 Show Your Support
If you find this project useful, please consider showing your support:

⭐ Star this repo
📢 Share it with others
💡 Contribute via pull requests

yaml
Copy
Edit

---
