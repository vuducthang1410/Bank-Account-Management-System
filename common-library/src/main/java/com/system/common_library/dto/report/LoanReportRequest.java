package com.system.common_library.dto.report;

import com.system.common_library.enums.ObjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanReportRequest {
    private String loanId;                 // ID Khoản vay
    private String customerId;             // ID Khách hàng hoặc tên người vay
    private Double minLoanAmount;      // Số tiền vay (từ)
    private Double maxLoanAmount;      // Số tiền vay (đến)
    private String loanType;               // Loại khoản vay
    private LocalDate startDate;
    private LocalDate endDate;
    private ObjectStatus loanStatus;             // Trạng thái khoản vay
}
