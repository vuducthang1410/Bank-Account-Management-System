package org.demo.loanservice.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LoanProductRp implements Serializable {
    private String productId;
    private String productName;
    private String productDescription;
    private String formLoan;
    private String loanLimit;
    private List<InterestRateRp> interestRate;
    private String interestRateUnit;
    private Integer termLimit;
    private String utilities;
    private String productUrlImage;
    private String loanCondition;
    private String applicableObjects;
    private String createdDate;
}
