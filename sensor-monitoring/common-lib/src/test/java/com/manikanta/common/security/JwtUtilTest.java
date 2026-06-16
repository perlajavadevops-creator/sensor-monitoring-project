package com.manikanta.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "this-is-a-very-secret-key-that-must-be-long-enough-for-hs256";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600000L);
    }

    @Test
    void testGenerateAndValidateToken() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtUtil.isTokenValid("invalid-token"));
    }
}
