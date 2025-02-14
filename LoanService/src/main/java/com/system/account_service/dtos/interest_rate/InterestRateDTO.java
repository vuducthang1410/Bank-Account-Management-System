package com.system.account_service.dtos.interest_rate;

import com.system.account_service.entities.type.Unit;
import com.system.account_service.utils.MessageKeys;
import com.system.account_service.validator.annotation.EnumValidator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestRateDTO {
    @DecimalMin(value = "0.0", message = MessageKeys.MESSAGES_SCOPE_INTEREST_RATE)
    @NotNull(message = MessageKeys.MESSAGES_BLANK_INTEREST_RATE)
    private BigDecimal rate;

    @EnumValidator(enumClass = Unit.class, message = MessageKeys.MESSAGES_ENUM_UNIT)
    @NotBlank(message = MessageKeys.MESSAGES_BLANK_UNIT)
    @Getter(AccessLevel.NONE)
    private String unit;

    private Boolean isActive;

    public String getUnit() {
        return unit.toUpperCase();
    }
}
