package org.demo.loanservice.repositories;
import org.demo.loanservice.dto.enumDto.LoanStatus;
import org.demo.loanservice.dto.projection.LoanAmountInfoProjection;
import org.demo.loanservice.entities.LoanDetailInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LoanDetailInfoRepository extends JpaRepository<LoanDetailInfo, String> {
    String queryFetchMaxLoanLimitAndCurrentLoanAmount= """
            SELECT COALESCE(COUNT(tldi.loan_amount), 0) AS totalLoanedAmount,
                   tfi.loan_amount_max AS loanAmountMax\s
            FROM tbl_financial_info tfi\s
            LEFT JOIN tbl_loan_detail_info tldi ON tfi.id = tldi.financial_info_id\s
                 AND tldi.loan_status = 'PAID_OFF'\s
                 AND tldi.is_deleted = FALSE
            WHERE tfi.customer_id = :customerId\s
            AND tfi.is_deleted = FALSE
            GROUP BY tfi.id, tfi.loan_amount_max;
            """;
    Optional<LoanDetailInfo> findByIdAndIsDeleted(String loanId,Boolean isDeleted);
    @Query(value = queryFetchMaxLoanLimitAndCurrentLoanAmount, nativeQuery = true)
    Optional<LoanAmountInfoProjection> getMaxLoanLimitAndCurrentLoanAmount(String customerId);

    Page<LoanDetailInfo> findAllByIsDeletedFalseAndLoanStatus(LoanStatus loanStatus, Pageable pageable);
    Page<LoanDetailInfo> findAllByIsDeletedFalseAndFinancialInfo_IdIn(List<String> financialInfoIdList, Pageable pageable);
}
