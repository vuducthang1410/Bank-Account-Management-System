package org.demo.loanservice.dto.request;

import java.math.BigDecimal;

public class LoanProductRq {
    private String nameLoanProduct;
    private BigDecimal loanLimit;
    private String description;
    private String interestRateId;
    private String utilities;
    private String loanCondition;
    private String loanForm;
    private String applicableObjects;
}
