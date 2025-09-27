package org.accountpr.demo.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import org.accountpr.demo.model.enums.PaymentType;

import java.time.LocalDate;


@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "is_credit", nullable = false)
    private Boolean isCredit;

    @Column(name = "payed_at")
    private LocalDateTime payedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentType type;
    @Column(name = "expired")
    private Boolean expired;
    public static PaymentBuilder builder(Long accountId, LocalDate paymentDate,
                                         BigDecimal amount, Boolean isCredit,LocalDateTime payedAt, PaymentType type, Boolean expired) {
        return hiddenBuilder()
                .accountId(accountId)
                .paymentDate(paymentDate)
                .amount(amount)
                .isCredit(isCredit)
                .payedAt(payedAt)
                .type(type)
                .expired(expired);
    }
}

