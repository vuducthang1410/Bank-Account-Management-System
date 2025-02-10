package org.demo.loanservice.services;

import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.IndividualCustomerInfoRq;

public interface ILoanDetailInfoService{
    DataResponseWrapper<Object> registerIndividualCustomerLoan(IndividualCustomerInfoRq individualCustomerInfoRq, String transactionId);

    DataResponseWrapper<Object> approveIndividualCustomerDisbursement(String id, String transactionId);
}
