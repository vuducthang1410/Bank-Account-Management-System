package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.InterestRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRateRepository extends JpaRepository<InterestRate, String> {
}
