package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.LoanProductRq;
import org.demo.loanservice.repositories.LoanProductRepository;
import org.demo.loanservice.services.ILoanProductService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanProductServiceImpl implements ILoanProductService {
    private final LoanProductRepository loanProductRepository;
    @Override
    public DataResponseWrapper<Object> save(LoanProductRq loanProductRq, String transactionId) {

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
}
