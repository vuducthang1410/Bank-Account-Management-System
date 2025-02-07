package org.demo.loanservice.dto.request;

import lombok.Data;
import org.demo.loanservice.dto.enumDto.RequestStatus;

@Data
public class ApproveFinancialInfoRq {
    private String financialInfoId;
    private String statusFinancialInfo;
    private String note;
}
