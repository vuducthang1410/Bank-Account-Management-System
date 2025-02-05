package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.CustomerLoanInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerLoanInfoRepository extends JpaRepository<CustomerLoanInfo, String> {
}
