# 🔐 Authentication Service

A Spring Boot microservice for handling user registration, login, and JWT-based stateless authentication. Includes support for RS256 key-pair signing and a public key endpoint (JWKS) for distributed token validation.

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

- ✅ User registration and login
- 🔐 JWT generation (HS256 or RS256)
- 🔑 JWKS endpoint to expose public key
- 🧾 Role-based structure (optional)
- ♻️ Support for refresh tokens (optional)
- 🛡️ BCrypt password hashing
- 🧱 Clean layered architecture
- 🌍 Global exception handling

---

## 🧰 Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Data JPA**
- **JWT (Nimbus / JJWT)**
- **MySQL / H2**
- **Maven**
- **JUnit 5 + Mockito**

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 17+
- Maven 3.8+
- MySQL (or H2)
- Redis (optional, for refresh tokens or blacklists)
- RSA keypair (for RS256)

---

### ▶️ Build & Run

#### 📥 Clone the repository

```bash
git clone https://github.com/your-username/auth-service.git
cd auth-service
mvn clean install
