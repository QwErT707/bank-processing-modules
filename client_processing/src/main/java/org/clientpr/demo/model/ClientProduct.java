package org.clientpr.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.clientpr.demo.model.enums.ProductKey;
import org.clientpr.demo.model.enums.ProductStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_products")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class ClientProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "open_date", nullable = false)
    private LocalDateTime openDate;
    @Column(name = "close_date")
    private LocalDateTime closeDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;
    public static ClientProductBuilder builder(Long clientId, Long productId,
                                               LocalDateTime openDate, LocalDateTime closeDate,ProductStatus status){
        return hiddenBuilder().clientId(clientId).productId(productId).openDate(openDate)
                .closeDate(closeDate).status(status);}}

