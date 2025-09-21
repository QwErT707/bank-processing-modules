package org.accountpr.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {

    private Long id;
    @NotNull
    private Long clientId;
    @NotNull
    private Long productId;
    @NotNull
    private BigDecimal balance;
    @NotNull
    private BigDecimal interestRate;
    @NotNull
    private Boolean isRecalc;
    @NotNull
    private Boolean cardExist;
    @NotBlank
    private String status;
}
