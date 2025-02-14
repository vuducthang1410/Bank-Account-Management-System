package com.system.account_service.repositories;

import com.system.account_service.entities.AccountDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountDetailRepository extends JpaRepository<AccountDetails, String> {
    Optional<AccountDetails> findAccountDetailsByAccountDetailIdAndDeleted(String id, boolean deleted);

    Page<AccountDetails> findAllByDeleted(Boolean deleted, Pageable pageable);

    Boolean existsByAccountNumber(String accountNumber);
}
