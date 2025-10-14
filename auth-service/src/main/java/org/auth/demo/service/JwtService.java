package org.auth.demo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secret:default-secret-key}")
    private String jwtSecret;
    @Value("${jwt.expiration-hours:24}")
    private int expirationHours;
    public String generateToken(String serviceName, String[] permissions) {
        log.info("🔐 Generating JWT token for service: {}", serviceName);
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            long expirationMs = System.currentTimeMillis() + ((long) expirationHours * 60 * 60 * 1000);
            Date expirationDate = new Date(expirationMs);
            Map<String, Object> claims = new HashMap<>();
            claims.put("serviceName", serviceName);
            claims.put("permissions", permissions);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuer("auth-service")
                    .setIssuedAt(new Date())
                    .setExpiration(expirationDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            log.info("✅ JWT token generated successfully for service: {}", serviceName);
            return token;
        } catch (Exception e) {
            log.error("❌ Failed to generate JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate token", e);
        }
    }
}
