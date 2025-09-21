package org.creditpr.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="payments_registry")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class PaymentRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "product_registry_id", nullable = false)
    private Long productRegistryId;
    @NotNull
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
    @NotNull
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @NotNull
    @Column(name = "interest_rate_amount", nullable = false)
    private BigDecimal interestRateAmount;
    @NotNull
    @Column(name = "debt_amount", nullable = false)
    private BigDecimal debtAmount;
    @NotNull
    @Column(name = "expired", nullable = false)
    private Boolean expired;
    @Column(name = "payment_expiration_date")
    private LocalDateTime paymentExpirationDate;

public static PaymentRegistryBuilder builder(Long productRegistryId, LocalDateTime paymentDate,
                                             BigDecimal amount, BigDecimal interestRateAmount,
                                             BigDecimal debtAmount, Boolean expired,
                                             LocalDateTime paymentExpirationDate){
    return hiddenBuilder().productRegistryId(productRegistryId).paymentDate(paymentDate).amount(amount).interestRateAmount(interestRateAmount).debtAmount(debtAmount).expired(expired).paymentExpirationDate(paymentExpirationDate);
}
}
