package org.accountpr.demo.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.accountpr.demo.model.enums.TransactionStatus;
import org.accountpr.demo.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;

    @NotNull
    private Long accountId;

    //@NotNull
    private Long cardId;

    @NotNull
    private TransactionType type;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotNull
    private TransactionStatus status;

    @NotNull
    private LocalDateTime timestamp;
}
