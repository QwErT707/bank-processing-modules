package org.creditpr.demo.repository;

import org.creditpr.demo.model.ProductRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRegistryRepository extends JpaRepository<ProductRegistry, Long> {
    List<ProductRegistry> findByClientId(Long clientId);
    List<ProductRegistry> findByProductId(Long productId);
    Optional<ProductRegistry> findByAccountId(Long accountId);
    boolean existsByClientIdAndProductId(Long clientId, Long productId);
}


