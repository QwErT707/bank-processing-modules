package org.auth.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "auth-service");
    }

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("message", "Auth service is working!");
    }
}
