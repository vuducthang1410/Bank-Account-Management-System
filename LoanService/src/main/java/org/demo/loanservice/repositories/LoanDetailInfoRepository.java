package org.demo.loanservice.repositories;
import org.demo.loanservice.entities.LoanDetailInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanDetailInfoRepository extends JpaRepository<LoanDetailInfo, String> {
    Optional<LoanDetailInfo> findByIdAndIsDeleted(String loanId,Boolean isDeleted);
}
