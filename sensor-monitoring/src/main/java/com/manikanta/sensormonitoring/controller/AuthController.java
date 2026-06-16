package com.manikanta.sensormonitoring.controller;

import com.manikanta.sensormonitoring.dto.JwtResponse;
import com.manikanta.sensormonitoring.dto.LoginRequest;
import com.manikanta.sensormonitoring.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Login to obtain a JWT token (demo: admin / admin123)")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login and get a JWT token")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
