package org.demo.loanservice.validatedCustom;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.demo.loanservice.validatedCustom.interfaceValidate.InterestRateValidation;

import java.math.BigDecimal;

public class InterestRateValidator implements ConstraintValidator<InterestRateValidation, BigDecimal> {
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext constraintValidatorContext) {
        return value.compareTo(BigDecimal.ZERO)>=0;
    }
}
