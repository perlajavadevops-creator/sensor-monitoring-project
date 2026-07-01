package com.manikanta.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<String> authFallback() {
        return Mono.just("Authentication service is currently unavailable. Please try again later.");
    }

    @GetMapping("/device")
    public Mono<String> deviceFallback() {
        return Mono.just("Device service is currently unavailable. Please try again later.");
    }

    @GetMapping("/sensor")
    public Mono<String> sensorFallback() {
        return Mono.just("Sensor service is currently unavailable. Please try again later.");
    }
}
