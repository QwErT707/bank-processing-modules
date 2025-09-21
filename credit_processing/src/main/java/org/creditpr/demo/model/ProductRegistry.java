package org.creditpr.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="products_registry")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class ProductRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name="client_id", nullable = false)
    private Long clientId;

    @NotNull
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @NotNull
    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;
    @NotNull
    @Column(name = "open_date", nullable = false)
    private LocalDateTime openDate;

    public static ProductRegistryBuilder builder(Long clientId, Long accountId, Long productId,
                                                 BigDecimal interestRate,LocalDateTime openDate){
        return hiddenBuilder().clientId(clientId).accountId(accountId).productId(productId).interestRate(interestRate).openDate(openDate);
    }
}
