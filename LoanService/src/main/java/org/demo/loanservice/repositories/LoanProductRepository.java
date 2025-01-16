package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanProductRepository extends JpaRepository<LoanProduct, String> {
}
