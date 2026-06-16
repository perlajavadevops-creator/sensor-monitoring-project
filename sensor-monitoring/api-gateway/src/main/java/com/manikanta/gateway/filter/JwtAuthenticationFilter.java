package com.manikanta.gateway.filter;

import com.manikanta.gateway.config.GatewaySecurityProperties;
import com.manikanta.gateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Authentication gate for the whole gateway.
 *
 * This is a {@link GlobalFilter}, meaning it runs for every route defined
 * in application.yml - there is no way to add a new downstream route and
 * accidentally forget to protect it. A request only reaches device-service
 * or sensor-service if it carries a valid "Bearer" JWT; everything else is
 * rejected here with 401, before a single byte is proxied downstream.
 *
 * Paths listed in gateway.security.public-paths (the auth-service login
 * endpoint, actuator health) are let through untouched, since they're the
 * mechanism by which a client *gets* a token in the first place.
 *
 * On success, the caller's username (extracted from the token) is attached
 * as an X-Authenticated-User header so downstream services can trust the
 * gateway's verdict without re-parsing the token, while still keeping
 * their own JwtAuthenticationFilter (from common-lib) active as a
 * defense-in-depth layer for any traffic that isn't routed through the
 * gateway.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final GatewaySecurityProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.debug("Gateway received request for path: {}", path);

        if (isPublic(path)) {
            log.debug("Path {} is public, bypassing authentication", path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return reject(exchange, "Missing bearer token. Authenticate via /api/v1/auth/login first.");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            log.warn("Invalid JWT token for path: {}", path);
            return reject(exchange, "Invalid or expired token.");
        }

        String username = jwtUtil.extractUsername(token);
        log.debug("Token validated for user: {}. Proxying request.", username);

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-Authenticated-User", username)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isPublic(String path) {
        return securityProperties.getPublicPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> reject(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        String body = String.format(
                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}", message);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public int getOrder() {
        // Run before Spring Cloud Gateway's routing filters so an
        // unauthenticated request never even reaches the proxy logic.
        return -1;
    }
}