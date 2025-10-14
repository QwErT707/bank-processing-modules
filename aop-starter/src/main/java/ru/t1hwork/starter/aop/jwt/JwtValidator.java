package ru.t1hwork.starter.aop.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;

@Component
@Slf4j
public class JwtValidator {
    @Value("${jwt.secret:default-secret-key}")
    private String jwtSecret;
    public boolean validateToken(String token) {
        try {
            log.info("🔐 Validating JWT token...");
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            log.info("✅ JWT validation successful");
            return true;
        } catch (Exception e) {
            log.error("❌ JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean hasRequiredPermission(String token, String requiredPermission) {
        try {
            Claims claims = parseToken(token);
            List<String> permissions = claims.get("permissions", List.class);
            if (permissions != null) {
                boolean hasPermission = permissions.contains(requiredPermission);
                log.info("🔍 Permission check: required='{}', has={}, result={}",
                        requiredPermission, permissions, hasPermission);
                return hasPermission;
            }
            return false;
        } catch (Exception e) {
            log.error("❌ Permission check failed: {}", e.getMessage());
            return false;
        }
    }
    public List<String> getPermissions(String token) {
        try {
            Claims claims = parseToken(token);
            List<String> permissions = claims.get("permissions", List.class);
            return permissions != null ? permissions : List.of();
        } catch (Exception e) {
            log.error("❌ Failed to get permissions from token: {}", e.getMessage());
            return List.of();
        }
    }

    public String getServiceName(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get("serviceName", String.class);
        } catch (Exception e) {
            log.error("❌ Failed to get service name from token: {}", e.getMessage());
            return null;
        }
    }

}
