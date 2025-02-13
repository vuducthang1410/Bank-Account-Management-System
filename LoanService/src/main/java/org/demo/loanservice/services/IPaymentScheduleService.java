package org.demo.loanservice.services;

import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.DeftRepaymentRq;
import org.demo.loanservice.entities.LoanDetailInfo;
import org.springframework.transaction.annotation.Transactional;

public interface IPaymentScheduleService {
    void createDeftRepaymentInfo(LoanDetailInfo loanDetailInfo);

    @Transactional
    DataResponseWrapper<Object> automaticallyRepaymentDeftPeriodically(DeftRepaymentRq deftRepaymentRq, String transactionId);

    DataResponseWrapper<Object> getListPaymentScheduleByLoanDetailInfo(String loanInfoId, Integer pageSize, Integer pageNumber, String transactionId);
}
