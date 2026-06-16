# Sensor Monitoring Microservices

A distributed IoT sensor monitoring system built with Spring Boot, Kafka, and PostgreSQL.

## Architecture
- **`auth-service` (8081)**: JWT Token issuance.
- **`device-service` (8082)**: Device management (CRUD).
- **`sensor-service` (8083)**: Sensor data collection, simulation, and Kafka processing.
- **`common-lib`**: Shared security, DTOs, and exception handling.

## Prerequisites
- Java 21
- Maven
- Docker & Docker Compose

## Quick Start

1. **Build the modules**:
   ```bash
   mvn clean install
   ```

2. **Start the ecosystem**:
   ```bash
   docker compose up --build
   ```

## Testing Guide

### 1. Authentication
All protected endpoints require a Bearer token.
- **Endpoint**: `POST http://localhost:8081/api/v1/auth/login`
- **Body**:
  ```json
  {
    "username": "admin",
    "password": "admin123"
  }
  ```
- **Action**: Copy the `token` from the response for use in the `Authorization` header: `Bearer <token>`.

### 2. Register a Device
Before sensors can report data, a device must exist in the `device-service`.
- **Endpoint**: `POST http://localhost:8082/api/v1/devices`
- **Header**: `Authorization: Bearer <token>`
- **Body**:
  ```json
  {
    "deviceCode": "SN-001",
    "name": "Warehouse Temp Sensor",
    "location": "Zone A",
    "status": "ACTIVE"
  }
  ```

### 3. Sensor Data Flow
The `sensor-service` automatically polls the `device-service` every 10 seconds (configurable via `SENSOR_INTERVAL_MS`) to generate simulated data for all active devices.

#### Manually Trigger a Reading
- **Endpoint**: `POST http://localhost:8083/api/v1/sensors/simulate/SN-001`
- **Header**: `Authorization: Bearer <token>`

#### View All Readings
- **Endpoint**: `GET http://localhost:8083/api/v1/sensors/readings`
- **Header**: `Authorization: Bearer <token>`

#### View Critical Alerts
- **Endpoint**: `GET http://localhost:8083/api/v1/sensors/alerts`
- **Header**: `Authorization: Bearer <token>`

### 4. Infrastructure Monitoring
- **Kafka UI**: `http://localhost:8089`
  - View topics: `sensor-readings` and `sensor-alerts`.
- **Postgres**: Accessible at `localhost:5432` (User: `sensoruser`, Pass: `sensorpass`).

## Communication Flow
1. **Sync**: `sensor-service` -> Feign Client -> `device-service` (To get list of devices).
2. **Async**: `sensor-service` (Producer) -> Kafka -> `sensor-service` (Consumer) -> PostgreSQL.
3. **Security**: Every request is intercepted by the `RateLimitingFilter` and `JwtAuthenticationFilter` provided by `common-lib`.

## Error Handling
- `401 Unauthorized`: Missing or invalid token.
- `429 Too Many Requests`: Rate limit exceeded (20 req/sec per IP).
- `404 Not Found`: Device or resource missing.
