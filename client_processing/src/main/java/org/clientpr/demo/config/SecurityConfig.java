package org.clientpr.demo.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.t1hwork.starter.aop.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @PostConstruct
    public void init() {
        log.info("✅🔒 SecurityConfig loaded for client-service");
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("🔒 Configuring Spring Security for client-service...");
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**",
                                "/health",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.error("🚫 Authentication failed: {}", authException.getMessage());
                            response.setStatus(401);
                            response.getWriter().write("Authentication failed: " + authException.getMessage());
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.error("🚫 Access denied: {}", accessDeniedException.getMessage());
                            response.setStatus(403);
                            response.getWriter().write("Access denied: " + accessDeniedException.getMessage());
                        })
                );
        log.info("✅🔒 Spring Security configuration completed with JWT filter");
        return http.build();
    }
}