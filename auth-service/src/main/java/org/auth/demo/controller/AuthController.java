package org.auth.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.auth.demo.model.ServiceAuthRequest;
import org.auth.demo.model.ServiceAuthResponse;
import org.auth.demo.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwtService;
    private final Map<String, String> serviceCredentials=new HashMap<>();
    public AuthController(JwtService jwtService){
        this.jwtService=jwtService;
        serviceCredentials.put("credit-service", "credit-secret-123");
        serviceCredentials.put("client-service", "client-secret-123");
        serviceCredentials.put("account-service", "account-secret-123");   }

    @PostMapping("/token")
    public ResponseEntity<ServiceAuthResponse> generateToken(
            @Valid @RequestBody ServiceAuthRequest request){
        System.out.println("🔐 Received auth request for service: " + request.getServiceName());
        String expectedSecret= serviceCredentials.get(request.getServiceName());
        if(expectedSecret==null || !expectedSecret.equals(request.getServiceSecret())){
            System.out.println("❌ Invalid credentials for service: " + request.getServiceName());
            return ResponseEntity.status(401).build();
        }
        String[] permissions= getPermissionsForService(request.getServiceName());
        String token = jwtService.generateToken(request.getServiceName(), permissions);
        long expresIn=24*60*60;
        System.out.println("✅ Token generated for service: " + request.getServiceName());
        return ResponseEntity.ok(new ServiceAuthResponse(token, request.getServiceName(), expresIn));
            }

    private String[] getPermissionsForService(String serviceName) {
        return switch (serviceName){
            case "credit-service" -> new String[]{"read:clients", "write:credits"};
            case "client-service" -> new String[]{"read:clients", "write:clients"};
            case "account-service" -> new String[]{"read:accounts", "write:accounts"};
            default -> new String[]{};
        };
    }
}

