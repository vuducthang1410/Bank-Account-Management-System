package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.PaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, String> {
    List<PaymentSchedule> findByIsDeletedFalseAndIsPaidFalseAndDueDate(Timestamp dueDate);
    List<PaymentSchedule> findByIsDeletedFalseAndIsPaidFalseAndDueDateBefore(Timestamp date);
    Optional<PaymentSchedule> findByIdAndIsDeleted(String id,Boolean isDeleted);
}
