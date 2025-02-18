package org.demo.loanservice.dto.request;

import lombok.Data;

@Data
public class LoanInfoApprovalRq {
    private String loanDetailInfoId;
    private String requestStatus;
    private String note;
}
