package com.system.common_library.dto.report;

import com.system.common_library.dto.report.config.FieldName;
import com.system.common_library.enums.AccountType;
import com.system.common_library.enums.ObjectStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountReportResponse {
    @FieldName("Số tài khoản")
    private String accountNumber;

    @FieldName("Loại tài khoản")
    private AccountType accountType;

    @FieldName("Chi nhánh ngân hàng")
    private String bankBranch;

    @FieldName("Số dư")
    private Double balance;

    @FieldName("Ngày mở tài khoản")
    private LocalDateTime openedAt;

    @FieldName("Trạng thái")
    private ObjectStatus status;

    @FieldName("Chủ tài khoản")
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
}


