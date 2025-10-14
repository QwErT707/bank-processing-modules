package org.creditpr.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.creditpr.demo.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {
    private final TokenService tokenService;
    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate= new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(jwtInterceptor()));
        return restTemplate;
    }
    private ClientHttpRequestInterceptor jwtInterceptor() {
        return new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                                ClientHttpRequestExecution execution) throws IOException {
                String token = tokenService.getValidToken();
                if (token != null) {
                    request.getHeaders().set("Authorization", "Bearer " + token);
                    log.info("🔐 Added JWT token to request to: {}", request.getURI());
                }else {log.warn("⚠️ No JWT token available for request to: {}", request.getURI());}
                return execution.execute(request, body);
            }
        };
    }
}
