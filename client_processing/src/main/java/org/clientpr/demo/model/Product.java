package org.clientpr.demo.model;

import jakarta.annotation.Resource;
import jakarta.persistence.*;
import lombok.*;
import org.clientpr.demo.model.enums.DocumentType;
import org.clientpr.demo.model.enums.ProductKey;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductKey key;
   @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;
    @Column(name = "product_id", unique = true, nullable = false)
    private String productId;
   public static ProductBuilder builder(String name, ProductKey key, LocalDateTime createDate){
return hiddenBuilder().name(name).key(key).createDate(createDate);}
}
