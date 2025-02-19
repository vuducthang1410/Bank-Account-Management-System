package com.system.common_library.dto.report;

import com.system.common_library.dto.report.config.FieldName;
import com.system.common_library.enums.ObjectStatus;

import java.time.LocalDate;

public class LoanReportDTO {
    @FieldName("Mã khoản vay")
    private String loanId;

    @FieldName("Số tiền vay")
    private Double loanAmount;

    @FieldName("Loại khoản vay")
    private String loanType;

    @FieldName("Ngày bắt đầu vay")
    private LocalDate startDate;

    @FieldName("Trạng thái khoản vay")
    private ObjectStatus loanStatus;

    @FieldName("Số dư còn lại của khoản vay")
    private Double remainingBalance;

    @FieldName("Thanh toán hàng tháng")
    private Double monthlyPayment;

    @FieldName("Lãi suất")
    private Double interestRate;
}
