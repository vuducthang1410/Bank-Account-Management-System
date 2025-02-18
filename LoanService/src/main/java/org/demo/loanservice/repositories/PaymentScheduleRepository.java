package org.demo.loanservice.repositories;

import org.demo.loanservice.dto.projection.RepaymentScheduleProjection;
import org.demo.loanservice.entities.PaymentSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, String> {
    String queryGetListPaymentScheduleByLoanDetailInfoId = """
            SELECT rps.id,
                   rps.amount_interest_rate AS amountInterest,
                   rps.amount_repayment AS amountRepayment,
                   rps.due_date AS dueDate,
                   rps.is_paid AS isPaid,
                   rps.name,
                   rps.is_paid_interest AS isPaidInterest,
                   rps.payment_interest_date AS paymentInterestRate,
                   rps.payment_schedule_date AS paymentScheduleDate,
                   rps.status,
                   COALESCE(SUM(CASE WHEN lp.isPaid = false THEN lp.fined_amount ELSE 0 END), 0) AS amountFinedRemaining,
                   COALESCE(SUM(lp.fined_amount), 0) AS totalFinedAmount
            FROM RepaymentPaymentSchedule rps
                     LEFT JOIN LoanPenalties lp ON rps.id = lp.paymentScheduleId
            WHERE rps.isDeleted = false\s
              AND lp.isDeleted = false
              AND rps.customer_interest_rate = :loanDetailInfoId
            GROUP BY rps.id, rps.amount_interest_rate, rps.amount_repayment, rps.due_date,\s
                     rps.is_paid, rps.is_paid_interest, rps.payment_interest_date,\s
                     rps.payment_schedule_date, rps.status, rps.name;
            
            """;
    String countSizeListPaymentScheduleByLoanDetailInfoId = """
                SELECT count(*)
                FROM RepaymentPaymentSchedule rps
                WHERE rps.isDeleted = false
            """;


    List<PaymentSchedule> findByIsDeletedFalseAndIsPaidFalseAndDueDate(Timestamp dueDate);

    List<PaymentSchedule> findByIsDeletedFalseAndIsPaidFalseAndDueDateBefore(Timestamp date);

    Optional<PaymentSchedule> findByIdAndIsDeleted(String id, Boolean isDeleted);

    @Query(value = queryGetListPaymentScheduleByLoanDetailInfoId, countQuery = countSizeListPaymentScheduleByLoanDetailInfoId, nativeQuery = true)
    Page<RepaymentScheduleProjection> findPaymentScheduleByLoanDetailInfoId(String loanDetailInfoId, Pageable pageable);

}
