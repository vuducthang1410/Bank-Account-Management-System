package com.system.account_service.dtos.saving;

import com.system.account_service.dtos.account_detail.CreateAccountDetailDTO;
import com.system.account_service.utils.MessageKeys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSavingDTO {
    @NotBlank(message = MessageKeys.MESSAGES_BLANK_BANKING_ACCOUNT)
    private String bankingAccountId;

    @Valid
    private CreateAccountDetailDTO accountDetail;

    @NotBlank(message = MessageKeys.MESSAGES_BLANK_INTEREST_RATE)
    private String interestRateId;

    @DecimalMin(value = "100000.00", message = MessageKeys.MESSAGES_SCOPE_SAVING_MIN_BALANCE)
    private BigDecimal balance;

    private Date endDate;
}
