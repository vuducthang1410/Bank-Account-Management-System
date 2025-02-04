package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.LoanProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanProductRepository extends JpaRepository<LoanProduct, String> {
    Optional<LoanProduct> findByIdAndIsDeleted(String id, boolean isDeleted);

    Page<LoanProduct> findAllByIsDeleted(Boolean isDeleted,Pageable pageable);
}
