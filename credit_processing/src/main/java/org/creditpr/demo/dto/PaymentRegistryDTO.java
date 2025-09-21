package org.creditpr.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRegistryDTO {
    private Long id;

    @NotNull(message = "Product registry ID is required")
    @Positive(message = "Product registry ID must be positive")
    private Long productRegistryId;

    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Interest rate amount is required")
    @Positive(message = "Interest rate amount must be positive")
    private BigDecimal interestRateAmount;

    @NotNull(message = "Debt amount is required")
    @Positive(message = "Debt amount must be positive")
    private BigDecimal debtAmount;

    @NotNull(message = "Expired status is required")
    private Boolean expired;

    private LocalDateTime paymentExpirationDate;
}
