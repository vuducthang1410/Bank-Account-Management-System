package org.demo.loanservice.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanProductRq {
    private String nameLoanProduct;
    private BigDecimal loanLimit;
    private String description;
    private String utilities;
    private String loanCondition;
    private String loanForm;
    private String applicableObjects;
    private Integer termLimit;
}

