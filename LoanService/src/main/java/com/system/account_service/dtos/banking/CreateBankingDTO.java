package com.system.account_service.dtos.banking;

import com.system.account_service.dtos.account_detail.CreateAccountDetailDTO;
import com.system.account_service.utils.MessageKeys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBankingDTO {
    @Valid
    private CreateAccountDetailDTO accountDetail; 

    @NotBlank(message = MessageKeys.MESSAGES_BLANK_NICK_NAME)
    private String nickName;
}
