package com.manikanta.auth.service;

import com.manikanta.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final String DEMO_USERNAME = "admin";
    private static final String DEMO_PASSWORD = "admin123";

    private final JwtUtil jwtUtil;

    public String authenticate(String username, String rawPassword) {
        log.info("Attempting to authenticate user: {}", username);
        if (DEMO_USERNAME.equals(username) && DEMO_PASSWORD.equals(rawPassword)) {
            log.info("Authentication successful for user: {}", username);
            return jwtUtil.generateToken(username);
        }
        log.warn("Authentication failed for user: {}", username);
        throw new BadCredentialsException("Invalid username or password");
    }
}