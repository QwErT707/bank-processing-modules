package org.accountpr.demo.model;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_transaction_monitoring")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class CardTransactionMonitoring {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
        @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public static CardTransactionMonitoringBuilder builder(Long cardId,LocalDateTime transactionTime,
                                                           String transactionType, BigDecimal amount){
        return hiddenBuilder().cardId(cardId).transactionTime(transactionTime).transactionType(transactionType).amount(amount);
    }

}
