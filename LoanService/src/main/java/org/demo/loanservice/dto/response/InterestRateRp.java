package org.demo.loanservice.dto.response;

import lombok.Data;

@Data
public class InterestRateRp {
    private String id;
    private String interestRate;
    private String unit;
    private String isActive;
    private String minimumAmount;
    private String dateActive;
    private String minimumLoanTerm;
    private String createdDate;
}
