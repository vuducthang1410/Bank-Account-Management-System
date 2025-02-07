package org.demo.loanservice.repositories;
import org.demo.loanservice.entities.LoanDetailInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanDetailInfoRepository extends JpaRepository<LoanDetailInfo, String> {
}
