package com.system.common_library.dto.report;

import com.system.common_library.dto.report.config.FieldName;
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
public class TransactionReportResponse {
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

    @FieldName("Danh sách giao dịch")
    List<TransactionReportDTO> transactions;
}
