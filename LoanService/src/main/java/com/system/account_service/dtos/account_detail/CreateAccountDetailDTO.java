package com.system.account_service.dtos.account_detail;

import com.system.account_service.entities.type.AccountStatus;
import com.system.account_service.entities.type.AccountTypes;
import com.system.account_service.utils.MessageKeys;
import com.system.account_service.validator.annotation.EnumValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountDetailDTO {
    @NotBlank(message = MessageKeys.MESSAGES_BLANK_CUSTOMER_ID)
    private String customerId;

    @NotBlank(message = MessageKeys.MESSAGES_BLANK_ACCOUNT_NUMBER)
    @Size(
        min = 6,
        max = 20,
        message = MessageKeys.MESSAGES_SCOPE_ACCOUNT_NUMBER_SIZE
    )
    private String accountNumber;

    @NotBlank(message = MessageKeys.MESSAGES_BLANK_ACCOUNT_TYPE)
    @EnumValidator(enumClass = AccountTypes.class, message = MessageKeys.MESSAGES_ENUM_ACCOUNT_TYPE)
    @Getter(AccessLevel.NONE)
    private String accountType;

    private String branchName;

    @EnumValidator(enumClass = AccountStatus.class, message = MessageKeys.MESSAGES_ENUM_ACCOUNT_STATUS)
    @Getter(AccessLevel.NONE)
    private String status;

    public String getAccountType() {
        return accountType.toUpperCase();
    }

    public String getStatus() {
        return status.toUpperCase();
    }
}
