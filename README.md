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
