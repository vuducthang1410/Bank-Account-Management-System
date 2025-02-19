package com.system.common_library.dto.report;

import com.system.common_library.enums.AccountType;
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
public class AccountReportRequest {

    private AccountType accountType;
    private String bankBranch;
    private Double startBalance;
    private Double endBalance;
    private LocalDate startAt;
    private LocalDate endAt;
    private ObjectStatus status;
}
