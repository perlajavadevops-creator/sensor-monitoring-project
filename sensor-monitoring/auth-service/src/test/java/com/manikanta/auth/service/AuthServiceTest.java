package com.manikanta.auth.service;

import com.manikanta.common.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void testAuthenticateSuccess() {
        String username = "admin";
        String password = "admin123";
        String expectedToken = "mock-jwt-token";

        when(jwtUtil.generateToken(username)).thenReturn(expectedToken);

        String result = authService.authenticate(username, password);

        assertEquals(expectedToken, result);
        verify(jwtUtil).generateToken(username);
    }

    @Test
    void testAuthenticateFailure() {
        assertThrows(BadCredentialsException.class, () -> 
            authService.authenticate("wrong", "password")
        );
        verifyNoInteractions(jwtUtil);
    }
}
