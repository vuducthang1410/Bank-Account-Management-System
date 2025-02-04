package org.demo.loanservice.dto.request;

import lombok.Data;
import org.demo.loanservice.validatedCustom.interfaceValidate.UnitValidation;

import java.io.Serializable;

@Data
public class LoanTermRq implements Serializable {
    private Integer term;
    @UnitValidation
    private String unit;
}
