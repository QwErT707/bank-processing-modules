package org.clientpr.demo.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardCreationRequestDTO {
    private String requestId;

    @NotNull(message = "Account ID is required")
    private Long accountId;
    @NotNull(message = "Card type is required")
    private String cardType;

    private String currency = "RUB";
    private LocalDateTime timestamp;

}
