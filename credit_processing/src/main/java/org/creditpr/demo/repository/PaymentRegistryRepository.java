package org.creditpr.demo.repository;

import org.creditpr.demo.model.PaymentRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRegistryRepository extends JpaRepository<PaymentRegistry, Long> {
    List<PaymentRegistry> findByProductRegistryId(Long productRegistryId);
    //that strange things
    List<PaymentRegistry> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    List<PaymentRegistry> findByExpired(Boolean expired);
}
