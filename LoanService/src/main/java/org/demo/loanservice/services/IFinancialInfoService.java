package org.demo.loanservice.services;

import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.FinancialInfoRq;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFinancialInfoService{
    DataResponseWrapper<Object> saveInfoIndividualCustomer(FinancialInfoRq financialInfoRq, List<MultipartFile> incomeVerificationDocuments, String transactionId);

    DataResponseWrapper<Object> getAllInfoIsPending(Integer pageNumber, Integer pageSize, String transactionId);
}
