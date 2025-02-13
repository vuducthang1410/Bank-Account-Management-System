package org.demo.loanservice.dto.request;

import lombok.Data;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.validatedCustom.interfaceValidate.UnitValidation;

import java.math.BigDecimal;

@Data
public class IndividualCustomerInfoRq {
    private String loanProductId;
    private String formDeftRepayment;
    private BigDecimal loanAmount;
    private Integer loanTerm;
    @UnitValidation
    private String loanUnit;
}
