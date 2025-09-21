package org.accountpr.demo.model.dto;

import jakarta.validation.constraints.*;
import org.accountpr.demo.model.enums.PaymentSystem;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDTO {

    private Long id;

    @NotNull
    private Long accountId;

    @NotBlank
    @Size(max = 20)
    private String cardId;

    @NotNull
    private PaymentSystem paymentSystem;

    @NotBlank
    private String status;
}

