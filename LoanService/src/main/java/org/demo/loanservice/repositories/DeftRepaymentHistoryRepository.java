package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.DeftRepaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeftRepaymentHistoryRepository extends JpaRepository<DeftRepaymentHistory, String> {
}
