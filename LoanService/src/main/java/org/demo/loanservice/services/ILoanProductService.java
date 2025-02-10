package org.demo.loanservice.services;

import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.LoanProductRq;
import org.demo.loanservice.entities.LoanProduct;
import org.springframework.web.multipart.MultipartFile;

public interface ILoanProductService extends IBaseService<LoanProductRq> {
    DataResponseWrapper<Object> saveImageLoanProduct(String id, MultipartFile image,String transactionId);
    LoanProduct getLoanProductById(String id, String transactionId);
}
