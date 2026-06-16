# Sensor Monitoring Microservices

A distributed IoT sensor monitoring system built with Spring Boot, Spring Cloud Gateway, Kafka, and PostgreSQL.

## Architecture
- **`api-gateway` (8080)**: Single entry point for all clients. Validates the JWT on every request before it reaches a downstream service.
- **`auth-service` (internal 8081)**: JWT Token issuance.
- **`device-service` (internal 8082)**: Device management (CRUD).
- **`sensor-service` (internal 8083)**: Sensor data collection, simulation, and Kafka processing.
- **`common-lib`**: Shared security, DTOs, and exception handling (used by auth/device/sensor-service).

`auth-service`, `device-service`, and `sensor-service` no longer publish ports to the host - the only way in from outside the docker network is through `api-gateway` on port 8080.

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
All protected endpoints require a Bearer token, obtained through the gateway.
- **Endpoint**: `POST http://localhost:8080/api/v1/auth/login`
- **Body**:
  ```json
  {
    "username": "admin",
    "password": "admin123"
  }
  ```
- **Action**: Copy the `token` from the response for use in the `Authorization` header: `Bearer <token>`.

### 2. Register a Device
Before sensors can report data, a device must exist in the `device-service`. This request - like every other one below - goes through the gateway, which checks the token before forwarding it.
- **Endpoint**: `POST http://localhost:8080/api/v1/devices`
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
The `sensor-service` automatically polls the `device-service` every 10 seconds (configurable via `SENSOR_INTERVAL_MS`) to generate simulated data for all active devices. That polling is internal service-to-service traffic and does not go through the gateway.

#### Manually Trigger a Reading
- **Endpoint**: `POST http://localhost:8080/api/v1/sensors/simulate/SN-001`
- **Header**: `Authorization: Bearer <token>`

#### View All Readings
- **Endpoint**: `GET http://localhost:8080/api/v1/sensors/readings`
- **Header**: `Authorization: Bearer <token>`

#### View Critical Alerts
- **Endpoint**: `GET http://localhost:8080/api/v1/sensors/alerts`
- **Header**: `Authorization: Bearer <token>`

### 4. Infrastructure Monitoring
- **Kafka UI**: `http://localhost:8089`
  - View topics: `sensor-readings` and `sensor-alerts`.
- **Postgres**: Accessible at `localhost:5432` (User: `sensoruser`, Pass: `sensorpass`).

## Communication Flow
1. **External entry point**: every client request hits `api-gateway` (8080) first. Its `JwtAuthenticationFilter` rejects anything without a valid token (except `/api/v1/auth/login` and health checks) before it is proxied anywhere.
2. **Sync**: `sensor-service` -> Feign Client -> `device-service` directly, on the internal docker network (bypasses the gateway, as this is service-to-service traffic, not a client request).
3. **Async**: `sensor-service` (Producer) -> Kafka -> `sensor-service` (Consumer) -> PostgreSQL.
4. **Defense in depth**: downstream services still run their own `RateLimitingFilter` and `JwtAuthenticationFilter` from `common-lib`, so they remain protected even if something reached them directly (e.g. in a future deployment where the gateway is bypassed by mistake).

## Error Handling
- `401 Unauthorized`: Missing or invalid token (returned by the gateway before the request is even forwarded, or by a downstream service for non-gateway traffic).
- `429 Too Many Requests`: Rate limit exceeded (20 req/sec per IP, enforced by each downstream service).
- `404 Not Found`: Device or resource missing.

