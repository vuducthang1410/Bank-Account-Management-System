package com.system.common_library.dto.report;

import com.system.common_library.dto.report.config.FieldName;
import com.system.common_library.enums.ObjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanReportResponse {

    @FieldName("Tên khách hàng")
    private String customerName;

    @FieldName("Email")
    private String email;

    @FieldName("Số điện thoại")
    private String phoneNumber;

    @FieldName("Địa chỉ")
    private String address;

    @FieldName("CCCD")
    private String cccd;

    @FieldName("Ngày sinh")
    private LocalDate birthDate;

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

    @FieldName("Số dư còn lại")
    private Double remainingBalance;

    @FieldName("Thanh toán hàng tháng")
    private Double monthlyPayment;

    @FieldName("Lãi suất")
    private Double interestRate;

    @FieldName("Danh sách giao dịch")
    List<TransactionReportDTO> transactions;
}
