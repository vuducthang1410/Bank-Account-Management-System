package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.dto.request.LoanTermRq;
import org.demo.loanservice.repositories.LoanTermRepository;
import org.demo.loanservice.services.ILoanTermService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanTermServiceImpl implements ILoanTermService {

    private final LoanTermRepository loanTermRepository;
    @Override
    public DataResponseWrapper<Object> save(LoanTermRq loanTermRq, String transactionId) {
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
    public DataResponseWrapper<Object> update(String id, LoanTermRq loanTermRq, String transactionId) {
        return null;
    }

    @Override
    public DataResponseWrapper<Object> delete(String id, String transactionId) {
        return null;
    }
}
