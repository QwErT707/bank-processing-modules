package org.accountpr.demo.repository;

import org.accountpr.demo.model.Account;
import org.aop.annotations.Cached;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Cached
    List<Account> findByClientId(Long ClientId);
}
