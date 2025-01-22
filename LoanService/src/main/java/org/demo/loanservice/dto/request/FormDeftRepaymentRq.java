package org.demo.loanservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.demo.loanservice.common.MessageValue;

@Data
public class FormDeftRepaymentRq {
    @NotBlank(message = MessageValue.VALID_DTO_NAME_NOT_BLANK)
    private String formName;
    @NotBlank(message = MessageValue.VALID_DTO_DESCRIPTION_NOT_BLANK)
    private String description;
    @NotNull
    @Min(value = 0)
    private int value;
}
