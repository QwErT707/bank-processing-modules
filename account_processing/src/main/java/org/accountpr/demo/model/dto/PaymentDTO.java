package org.accountpr.demo.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.accountpr.demo.model.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long id;

    @NotNull
    private Long accountId;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotNull
    private Boolean isCredit;

    private LocalDateTime payedAt;

    @NotNull
    private PaymentType type;

    private Boolean expired;
}

