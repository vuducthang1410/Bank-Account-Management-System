package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.LoanProductRq;
import org.demo.loanservice.entities.LoanProduct;
import org.demo.loanservice.repositories.LoanProductRepository;
import org.demo.loanservice.services.ILoanProductService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class LoanProductServiceImpl implements ILoanProductService {
    private final LoanProductRepository loanProductRepository;
    @Override
    public DataResponseWrapper<Object> save(LoanProductRq loanProductRq, String transactionId) {

        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setLoanLimit(loanProductRq.getLoanLimit());
        loanProduct.setLoanCondition(loanProductRq.getLoanCondition().getBytes(StandardCharsets.UTF_8));
        loanProduct.setNameProduct(loanProductRq.getNameLoanProduct());
        loanProduct.setDescription(loanProductRq.getDescription().getBytes(StandardCharsets.UTF_8));

        return null;
    }

    @Override
    public DataResponseWrapper<Object> getById(String id, String transactionId) {
        return null;
    }

    @Override
    public DataResponseWrapper<Object> getAll(Integer pageNumber, Integer pageSize, String transactionId) {
        return null;
    }

    @Override
    public DataResponseWrapper<Object> active(String id, String transactionId) {
        return null;
    }

    @Override
    public DataResponseWrapper<Object> update(String id, LoanProductRq loanProductRq, String transactionId) {
        return null;
    }

    @Override
    public DataResponseWrapper<Object> delete(String id, String transactionId) {
        return null;
    }

    @Override
    public DataResponseWrapper<Object> saveImageLoanProduct(String id, MultipartFile image, String transactionId) {
        return null;
    }
}
