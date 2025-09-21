package org.clientpr.demo.repository;

import org.clientpr.demo.model.ClientProduct;
import org.clientpr.demo.model.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

    @Repository
    public interface ClientProductRepository extends JpaRepository<ClientProduct, Long> {
        List<ClientProduct> findByClientId(Long clientId);
        List<ClientProduct> findByProductId(Long productId);
        List<ClientProduct> findByStatus(ProductStatus status);
        List<ClientProduct> findByClientIdAndStatus(Long clientId, ProductStatus status);
        boolean existsByClientIdAndProductId(Long clientId, Long productId);
    }
