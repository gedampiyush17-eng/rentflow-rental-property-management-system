# 🏠 RentFlow — Rental & Property Management System

RentFlow is a full-stack rental and property management system designed to help property owners manage properties, rental units, tenants, leases, rent cycles, payments, and receipts from a single platform.

The system provides separate experiences for **Owners** and **Tenants**, with JWT-based authentication and a complete rental payment workflow.

---

## 🛠️ Tech Stack

### Backend

| Technology | Purpose |
|---|---|
| **Java 17** | Backend programming language |
| **Spring Boot** | Backend application framework |
| **Spring Web** | REST API development |
| **Spring Data JPA** | Database interaction |
| **Hibernate** | ORM |
| **Spring Security** | Authentication & authorization |
| **JWT** | Stateless authentication |
| **BCrypt** | Password hashing |
| **PostgreSQL** | Relational database |
| **Jakarta Validation** | Request validation |
| **Lombok** | Boilerplate reduction |
| **MapStruct** | DTO ↔ Entity mapping |
| **Swagger / OpenAPI** | API documentation |
| **Maven** | Dependency management & build |

### Frontend

| Technology | Purpose |
|---|---|
| **React** | Frontend UI |
| **Vite** | Frontend build tool |
| **JavaScript (ES6+)** | Frontend programming |
| **React Router** | Client-side routing |
| **Axios** | REST API communication |
| **HTML5** | UI structure |
| **CSS3** | Styling & responsive UI |

### Database & Development Tools

| Tool | Purpose |
|---|---|
| **PostgreSQL** | Application database |
| **pgAdmin** | Database management |
| **Postman** | API testing |
| **Swagger UI** | API testing & documentation |
| **IntelliJ IDEA** | Backend development |
| **VS Code** | Frontend development |
| **Git** | Version control |
| **GitHub** | Source code hosting |

---

## 🏗️ Architecture

RentFlow follows a **feature-based layered architecture** on the backend and a component/page-based architecture on the frontend.

### Backend

```text
com.rentflow
│
├── auth
├── common
├── property
├── unit
├── tenant
├── lease
├── payment
├── rentcycle
├── receipt
└── remainder
