package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.FinancialInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialInfoRepository extends JpaRepository<FinancialInfo, String> {

}
