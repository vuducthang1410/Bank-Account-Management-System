package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.InterestRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestRateRepository extends JpaRepository<InterestRate, String> {
    Optional<InterestRate> findInterestRateByIdAndIsDeleted(String id, boolean isDeleted);
    Page<InterestRate> findAllByIsDeleted(boolean isDeleted, Pageable pageable);
}
