package com.manikanta.auth.service;

import com.manikanta.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String DEMO_USERNAME = "admin";
    private static final String DEMO_PASSWORD = "admin123";

    private final JwtUtil jwtUtil;

    public String authenticate(String username, String rawPassword) {
        if (DEMO_USERNAME.equals(username) && DEMO_PASSWORD.equals(rawPassword)) {
            return jwtUtil.generateToken(username);
        }
        throw new BadCredentialsException("Invalid username or password");
    }
}