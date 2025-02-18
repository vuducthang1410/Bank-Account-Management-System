package org.demo.loanservice.services;

import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.IndividualCustomerInfoRq;
import org.demo.loanservice.dto.request.LoanInfoApprovalRq;

public interface ILoanDetailInfoService{
    DataResponseWrapper<Object> registerIndividualCustomerLoan(IndividualCustomerInfoRq individualCustomerInfoRq, String transactionId);
    DataResponseWrapper<Object> approveIndividualCustomerDisbursement(LoanInfoApprovalRq loanInfoApprovalRq, String transactionId);

    DataResponseWrapper<Object> getAllByLoanStatus(String loanStatus, Integer pageNumber, Integer pageSize, String transactionId);
}
