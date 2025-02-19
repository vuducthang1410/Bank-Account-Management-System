package org.demo.loanservice.repositories;

import org.demo.loanservice.dto.enumDto.RequestStatus;
import org.demo.loanservice.entities.FinancialInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialInfoRepository extends JpaRepository<FinancialInfo, String> {
    Page<FinancialInfo> findAllByIsDeletedAndRequestStatus(Boolean isDeleted, RequestStatus isApproved, Pageable pageable);

    Optional<FinancialInfo> findByIdAndIsDeleted(String id, Boolean isDeleted);

    Optional<FinancialInfo> findByIsDeletedAndCustomerIdAndIsExpiredFalse(Boolean isDeleted, String customerId);

    List<FinancialInfo> findAllByIsDeletedFalseAndCustomerId(String customerId);
}
