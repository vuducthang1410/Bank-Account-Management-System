package com.system.account_service.repositories;

import com.system.account_service.entities.InterestRates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestRateRepository extends JpaRepository<InterestRates, String> {
    Optional<InterestRates> findByInterestRateIdAndDeleted(String interestRateId, Boolean deleted);

    Page<InterestRates> findAllByDeleted(Boolean deleted, Pageable pageable);
}
