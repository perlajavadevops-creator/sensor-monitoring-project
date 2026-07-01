# Sensor Monitoring Microservices

A distributed IoT sensor monitoring system built with Spring Boot, Spring Cloud Gateway, Resilience4j, Kafka, and PostgreSQL.

## Architecture
- **`api-gateway` (8080)**: Single entry point for all clients. Routes requests to downstream services, pre-validates JWTs, and wraps every route with a Resilience4j Circuit Breaker.
- **`auth-service` (8081)**: JWT Token issuance.
- **`device-service` (8082)**: Device management (CRUD).
- **`sensor-service` (8083)**: Sensor data collection, simulation, and Kafka processing.
- **`common-lib`**: Shared security, DTOs, exception handling, and Resilience4j circuit-breaker configuration.

> **Note:** As of this update, all client traffic goes through the `api-gateway` on port **8080**. Direct calls to `8081`/`8082`/`8083` are no longer the supported entry points for external clients (they remain reachable for internal service-to-service and debugging purposes).

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
All protected endpoints require a Bearer token. All requests now go through the API Gateway.
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
Before sensors can report data, a device must exist in the `device-service`.
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
The `sensor-service` automatically polls the `device-service` every 10 seconds (configurable via `SENSOR_INTERVAL_MS`) to generate simulated data for all active devices. This poll (a Feign call) is wrapped by the `deviceClientCB` Resilience4j circuit breaker.

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
- **Circuit Breaker status**: `GET http://localhost:8080/actuator/circuitbreakers` (and per-service `/actuator/health`) to inspect `CLOSED` / `OPEN` / `HALF_OPEN` state for each configured breaker.

## Communication Flow
1. **Entry**: `Clients` -> `api-gateway` (:8080) -> routed by path to `auth-service`, `device-service`, or `sensor-service`.
2. **Sync**: `sensor-service` -> Feign Client (wrapped in `deviceClientCB` circuit breaker) -> `device-service` (To get list of devices).
3. **Async**: `sensor-service` (Producer) -> Kafka -> `sensor-service` (Consumer) -> PostgreSQL.
4. **Security**: Every request is validated once at the `api-gateway` (JWT pre-check) and again by the `JwtAuthenticationFilter` and `RateLimitingFilter` provided by `common-lib` inside each downstream service (defense in depth).
5. **Resilience**: Every Gateway route and the `sensor-service` -> `device-service` Feign call is wrapped in a Resilience4j Circuit Breaker. When a downstream dependency is unhealthy, the breaker opens and a fallback response is returned instead of letting the failure cascade.

## Resilience (Circuit Breaker)
- **Library**: Resilience4j, configured in `common-lib` and applied both at the Gateway (per route) and around the `sensor-service` -> `device-service` Feign client.
- **Breakers**: `authServiceCB`, `deviceServiceCB`, `sensorServiceCB` (Gateway routes) and `deviceClientCB` (Feign client inside `sensor-service`).
- **Behavior**: Sliding-window failure-rate based; when the failure threshold is breached the breaker moves to `OPEN` and short-circuits calls to a fallback response until the wait duration elapses, then probes the dependency again from `HALF_OPEN`.

## Error Handling
- `401 Unauthorized`: Missing or invalid token.
- `429 Too Many Requests`: Rate limit exceeded (20 req/sec per IP).
- `404 Not Found`: Device or resource missing.
- `503 Service Unavailable`: Circuit breaker is `OPEN` for the requested route/dependency and the fallback response was served.
