package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.UserLoanInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoanInfoRepository extends JpaRepository<UserLoanInfo, String> {
}
