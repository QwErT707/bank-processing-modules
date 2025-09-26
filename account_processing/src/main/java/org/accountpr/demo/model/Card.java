package org.accountpr.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.accountpr.demo.model.enums.CardStatus;
import org.accountpr.demo.model.enums.PaymentSystem;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "card_id", unique = true, nullable = false, length = 20)
    private String cardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_system", nullable = false, length = 20)
    private PaymentSystem paymentSystem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    public static CardBuilder builder(Long accountId, String cardId, PaymentSystem paymentSystem, CardStatus cardStatus) {
        return hiddenBuilder()
                .accountId(accountId)
                .cardId(cardId)
                .paymentSystem(paymentSystem)
                .status(cardStatus);
    }
}

