package org.demo.loanservice.dto.request;

import lombok.Data;
import org.demo.loanservice.dto.enumDto.RequestStatus;

import java.math.BigDecimal;

@Data
public class ApproveFinancialInfoRq {
    private String financialInfoId;
    private String statusFinancialInfo;
    private String note;
    private BigDecimal loanAmountLimit;
}
