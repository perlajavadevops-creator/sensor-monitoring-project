package com.manikanta.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Validates JWTs issued by auth-service.
 *
 * This is a deliberately small, framework-agnostic class (no servlet API,
 * no Spring Security dependency) so it can run safely on the reactive
 * WebFlux stack that Spring Cloud Gateway requires. It must share the same
 * {@code jwt.secret} that auth-service uses to sign tokens - that's the
 * only contract between the two services.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Returns true only if the token's signature is valid (i.e. it was
     * issued by auth-service using the shared secret) and it has not
     * expired. Any parsing failure is treated as "invalid" rather than
     * propagating an exception, since the caller just needs a yes/no.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
