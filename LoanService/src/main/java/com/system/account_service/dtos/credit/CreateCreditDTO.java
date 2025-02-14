package com.system.account_service.dtos.credit;

import com.system.account_service.dtos.account_detail.CreateAccountDetailDTO;
import com.system.account_service.utils.MessageKeys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCreditDTO {
    @NotBlank(message = MessageKeys.MESSAGES_BLANK_BANKING_ACCOUNT)
    private String bankingAccountId;

    @Valid
    private CreateAccountDetailDTO accountDetail;

    @NotBlank(message = MessageKeys.MESSAGES_BLANK_INTEREST_RATE)
    private String interestRateId;

    @Positive(message = MessageKeys.MESSAGES_SCOPE_CREDIT_CREDIT_LIMIT)
    private BigDecimal creditLimit;

    @Positive(message = MessageKeys.MESSAGES_SCOPE_CREDIT_BILLING_CYCLE)
    private int billingCycle;
}
