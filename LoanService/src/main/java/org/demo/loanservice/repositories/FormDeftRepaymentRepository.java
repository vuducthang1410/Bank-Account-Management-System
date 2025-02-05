package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.FormDeftRepayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormDeftRepaymentRepository extends JpaRepository<FormDeftRepayment, String> {
    Optional<FormDeftRepayment> findByIdAndIsDeleted(String id,Boolean isDeleted);
    Page<FormDeftRepayment> findAllByIsDeleted(Boolean isDeleted, Pageable pageable);
}
