package org.accountpr.demo.model;

import jakarta.persistence.*;
import org.accountpr.demo.model.enums.TransactionStatus;
import org.accountpr.demo.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "card_id")//, nullable = false
    private Long cardId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public static TransactionBuilder builder(Long accountId, Long cardId,
                                             TransactionType type, BigDecimal amount,TransactionStatus status, LocalDateTime timestamp ) {
        return hiddenBuilder()
                .accountId(accountId)
                .cardId(cardId)
                .type(type)
                .amount(amount)
                .status(status)
                .timestamp(timestamp);
    }
}

