package org.clientpr.demo.repository;


import org.clientpr.demo.model.Product;
import org.clientpr.demo.model.enums.ProductKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByProductId(String productId);

    List<Product> findByKey(ProductKey key);

    boolean existsByProductId(String productId);

    @Query("SELECT p FROM Product p WHERE p.name ILIKE %:name%")
    List<Product> findByNameContainingIgnoreCase(String name);
}
