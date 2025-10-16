package org.clientpr.demo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import ru.t1hwork.starter.aop.jwt.JwtAuthenticationFilter;
import ru.t1hwork.starter.aop.jwt.JwtValidator;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
//    @Bean
//    @Primary
//    public JwtAuthenticationFilter jwtAuthenticationFilter() {
//        return mock(JwtAuthenticationFilter.class);
//    }
//
//    @Bean
//    @Primary
//    public JwtValidator jwtValidator() {
//        return mock(JwtValidator.class);
//    }
}