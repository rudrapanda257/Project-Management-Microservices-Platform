# 🚀 Enterprise Task Management Platform

A comprehensive microservices-based task management system built with Spring Boot and React.

## 📋 Description

Enterprise Task Management Platform is a full-stack application designed for managing projects, tasks, and team collaboration. Built using microservices architecture, it provides scalability, maintainability, and high availability for enterprise-level project management.

## 🛠️ Tech Stack

### Backend

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **PostgreSQL 15**
- **Apache Kafka** (for event-driven architecture)
- **Netflix Eureka** (service discovery)
- **Spring Cloud Gateway** (API gateway)
- **Spring Cloud Config** (centralized configuration)
- **JWT** (authentication & authorization)

### Frontend

- **React 18**
- **Redux Toolkit** (state management)
- **Material-UI (MUI)** (UI components)
- **React Router v6** (routing)
- **Axios** (HTTP client)
- **Recharts** (data visualization)

### DevOps & Tools

- **Docker & Docker Compose**
- **Maven** (build tool)
- **Git** (version control)
- **Vite** (frontend build tool)

## 🏗️ Architecture

### Microservices

1. **User Service** (Port: 8081) - Authentication & user management
2. **Project Service** (Port: 8082) - Projects & tasks management
3. **Notification Service** (Port: 8083) - Notifications via Kafka
4. **API Gateway** (Port: 8080) - Single entry point for all requests
5. **Eureka Server** (Port: 8761) - Service discovery
6. **Config Server** (Port: 8888) - Centralized configuration

### Architecture Diagram

```
                    ┌─────────────────┐
                    │   React Frontend│
                    │   (Port: 3000)  │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │   API Gateway   │
                    │   (Port: 8080)  │
                    └────────┬────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
     ┌──────▼──────┐  ┌─────▼─────┐  ┌──────▼──────┐
     │User Service │  │Project Svc│  │Notification │
     │  (8081)     │  │  (8082)   │  │Service(8083)│
     └─────────────┘  └───────────┘  └──────┬──────┘
            │                │                │
            └────────────────┼────────────────┘
                             │
                    ┌────────▼────────┐
                    │   PostgreSQL    │
                    │   (Port: 5432)  │
                    └─────────────────┘

     ┌─────────────────────────────────────────┐
     │         Kafka (Port: 9092)              │
     └─────────────────────────────────────────┘

     ┌─────────────────────────────────────────┐
     │    Eureka Server (Port: 8761)           │
     └─────────────────────────────────────────┘
```

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Docker Desktop
- Maven 3.8+
- Git

### Installation

_Setup instructions coming soon..._

### Running the Application

_Running instructions coming soon..._

## 📂 Project Structure

```
task-management-platform/
├── user-service/
├── project-service/
├── notification-service/
├── api-gateway/
├── eureka-server/
├── config-server/
├── task-management-frontend/
├── pom.xml
└── README.md
```

## ✨ Features

### Completed

- ✅ Project setup with multi-module Maven structure
- ✅ PostgreSQL database schema with 7 tables
- ✅ Git repository initialized

### In Progress

- 🔄 User authentication with JWT
- 🔄 Project and task management
- 🔄 Real-time notifications

### Planned

- ⏳ File attachments (AWS S3)
- ⏳ Comments and activity timeline
- ⏳ Dashboard with analytics
- ⏳ Docker containerization
- ⏳ CI/CD pipeline

## 📝 License

This project is for educational purposes.

## 👥 Authors

Rudra Narayan Panda

## 🙏 Acknowledgments

- Spring Boot Documentation
- React Documentation
- Material-UI Documentation

---

**Last Updated:** December 4, 2024
