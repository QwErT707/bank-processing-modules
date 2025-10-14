package ru.t1hwork.starter.aop.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtValidator jwtValidator;
    private final List<String> excludedEndpoints = Arrays.asList(
            "/v3/api-docs", "/swagger-ui", "/actuator", "/health", "/error"
    );
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        log.info("🔍 Checking endpoint: {} {}", method, requestUri);
        if (isExcludedEndpoint(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("❌ Missing or invalid Authorization header");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }
        String token = authHeader.substring(7);
        if (!jwtValidator.validateToken(token)) {
            log.error("❌ Invalid JWT token");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid JWT token");
            return;
        }
        try {
            String serviceName = jwtValidator.getServiceName(token);
            List<String> permissions = jwtValidator.getPermissions(token);
            List<SimpleGrantedAuthority> authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(serviceName, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("✅ JWT authentication successful for {} {} - Service: {}", method, requestUri, serviceName);
        } catch (Exception e) {
            log.error("❌ Failed to set authentication: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Failed to set authentication");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isExcludedEndpoint(String requestUri) {
        return excludedEndpoints.stream().anyMatch(requestUri::contains);
    }

    private boolean isInternalServiceRequest(String requestUri) {
        return requestUri.contains("/api/") && !requestUri.contains("/api/auth/");
    }

    private String getRequiredPermission(String requestUri, String method) {
        if (requestUri.contains("/api/clients/") && "GET".equalsIgnoreCase(method)) {
            return "read:clients";
        } else if (requestUri.contains("/api/clients/") && ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method))) {
            return "write:clients";
        }
        return null;
    }
}
