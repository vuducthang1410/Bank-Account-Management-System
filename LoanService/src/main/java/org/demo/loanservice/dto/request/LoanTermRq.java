package org.demo.loanservice.dto.request;

import lombok.Data;

@Data
public class LoanTermRq {
    private String loanProductId;
    private String term;
    private String unit;
}
