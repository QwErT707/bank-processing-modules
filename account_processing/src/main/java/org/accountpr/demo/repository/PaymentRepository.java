package org.accountpr.demo.repository;

import org.accountpr.demo.model.Payment;
import org.accountpr.demo.model.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByAccountId(Long accountId);

    List<Payment> findByType(PaymentType type);

    List<Payment> findByIsCredit(Boolean isCredit);
}
