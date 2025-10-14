package org.auth.demo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServiceAuthRequest {
    @NotBlank(message = "Service name is required")
    private String serviceName;
    @NotBlank(message = "Service secret is required")
    private String serviceSecret;
}
