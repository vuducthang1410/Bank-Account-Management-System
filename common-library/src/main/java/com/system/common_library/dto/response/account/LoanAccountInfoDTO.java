package com.system.common_library.dto.response.account;

import com.system.common_library.enums.ObjectStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class LoanAccountInfoDTO {
    @NonNull
    @NotBlank
    private String accountLoanId;
    @NonNull
    @NotBlank
    private ObjectStatus statusLoanAccount;
    @NonNull
    @NotBlank
    private String loanBalance;
    @NonNull
    @NotBlank
    private String loanAccountNumber;
}
