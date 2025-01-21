package org.demo.loanservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FormDeftRepaymentRq {
    @NotBlank(message = "{valid.dto.name.notBlank}")
    private String formName;
    @NotBlank(message = "{valid.dto.description.notBlank}")
    private String description;
    @NotNull
    @Min(value = 0)
    private int value;
}
