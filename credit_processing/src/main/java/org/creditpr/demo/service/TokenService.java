package org.creditpr.demo.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    @Value("${auth.service.url:http://auth-service:8080}")
    private String authServiceUrl;
    @Value("${service.credentials:credit-service:credit-secret-123}")
    private String serviceCredentials;
    private String currentToken;
    private long tokenExpiresAt;
    private RestTemplate restTemplate=new RestTemplate();
    @PostConstruct
    public void init(){
        log.info("\uD83D\uDD04Initializing JWT token for credit-service...");
        getValidToken();
    }

    public String getValidToken(){
        if(currentToken == null || System.currentTimeMillis()>tokenExpiresAt-60000){
            currentToken=fetchNewToken();
            tokenExpiresAt=System.currentTimeMillis()+(23*60*60*1000);
        }return currentToken;
    }

    private String fetchNewToken() {
        try{
            log.info("✅Attempting to fetch JWT token from auth-service: {}", authServiceUrl);

            String[] creds=serviceCredentials.split(":");
            String serviceName=creds[0];
            String serviceSecret=creds[1];
            Map<String, String> authRequest=new HashMap<>();
            authRequest.put("serviceName", serviceName);
            authRequest.put("serviceSecret",serviceSecret);
            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request=new HttpEntity<>(authRequest, headers);
            ResponseEntity<Map> response=restTemplate.exchange(
                    authServiceUrl+"/api/auth/token",
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            if(response.getStatusCode().is2xxSuccessful() && response.getBody()!= null){
                String token=(String) response.getBody().get("token");
                log.info("✅ Successfully obtained new JWT token for service: {}", serviceName);
                log.debug("🔐 Token: {}", token);
                return token;
            }else {
                log.error("❌ Failed to get token. Status: {}", response.getStatusCode());
            }
        }catch(Exception e){
            log.error("❌ Failed to obtain JWT token: {}", e.getMessage());
        }return null;
    }
}
