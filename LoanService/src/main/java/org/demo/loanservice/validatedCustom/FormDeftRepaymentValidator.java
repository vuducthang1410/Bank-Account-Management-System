package org.demo.loanservice.validatedCustom;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.demo.loanservice.dto.enumDto.FormDeftRepaymentEnum;
import org.demo.loanservice.validatedCustom.interfaceValidate.FormDeftRepaymentValidation;

import java.util.stream.Stream;

public class FormDeftRepaymentValidator implements ConstraintValidator<FormDeftRepaymentValidation,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null || value.isEmpty())
            return false;

        return Stream.of(
                FormDeftRepaymentEnum.PRINCIPAL_AND_INTEREST_MONTHLY.name(),
                FormDeftRepaymentEnum.PRINCIPAL_INTEREST_INITIAL.name(),
                FormDeftRepaymentEnum.PRINCIPAL_QUARTERLY_INTEREST_MONTHLY.name())
                .anyMatch(name->name.equalsIgnoreCase(value));
    }
}
