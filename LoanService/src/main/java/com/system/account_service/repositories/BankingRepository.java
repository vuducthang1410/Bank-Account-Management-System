package com.system.account_service.repositories;

import com.system.account_service.entities.BankingAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankingRepository extends JpaRepository<BankingAccount, String> {
    Optional<BankingAccount> findByAccountIdAndDeleted(String id, boolean deleted);

    Page<BankingAccount> findAllByDeleted(Boolean deleted, Pageable pageable);
}
