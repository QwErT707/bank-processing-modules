package org.creditpr.demo.repository;

import org.aop.annotations.Cached;
import org.creditpr.demo.model.ProductRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRegistryRepository extends JpaRepository<ProductRegistry, Long> {
    @Cached(ttl = 600000)
    List<ProductRegistry> findByClientId(Long clientId);
   @Cached(cacheName = "productRegistry.byProductId")
    List<ProductRegistry> findByProductId(Long productId);
    @Cached(cacheName = "productRegistry.byAccountId")
    Optional<ProductRegistry> findByAccountId(Long accountId);
    boolean existsByClientIdAndProductId(Long clientId, Long productId);
}


