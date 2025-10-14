package org.auth.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceAuthResponse {
    private String token;
    private String serviceName;
    private long expiresIn;
}
