package org.accountpr.demo.model;


import jakarta.persistence.*;
import lombok.*;
import org.accountpr.demo.model.enums.AccountStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "is_recalc", nullable = false)
    private Boolean isRecalc;

    @Column(name = "card_exist", nullable = false)
    private Boolean cardExist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    public static AccountBuilder builder(Long clientId, Long productId, BigDecimal balance, BigDecimal interestRate) {
        return hiddenBuilder()
                .clientId(clientId)
                .productId(productId)
                .balance(balance)
                .interestRate(interestRate)
                .isRecalc(false)
                .cardExist(false)
                .status(AccountStatus.ACTIVE);
    }
}

