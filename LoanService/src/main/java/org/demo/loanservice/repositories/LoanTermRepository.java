package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.LoanTerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanTermRepository extends JpaRepository<LoanTerm,String> {
    Page<LoanTerm> getAllByIsDeleted(Boolean isDeleted, Pageable pageable);
    Optional<LoanTerm> getLoanTermByIdAndIsDeleted(String id,Boolean isDeleted);
}
