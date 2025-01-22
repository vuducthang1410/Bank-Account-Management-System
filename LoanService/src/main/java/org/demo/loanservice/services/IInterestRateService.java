package org.demo.loanservice.services;

import jakarta.validation.Valid;
import org.demo.loanservice.dto.request.InterestRateRq;
import org.demo.loanservice.dto.response.DataResponseWrapper;

public interface IInterestRateService {

    DataResponseWrapper<Object> save(@Valid InterestRateRq interestRateRq, String transactionId);

    DataResponseWrapper<Object> getById(String id, String transactionId);

    DataResponseWrapper<Object> getAll(Integer pageNumber, Integer pageSize, String transactionId);

    DataResponseWrapper<Object> active(String id, String transactionId);

    DataResponseWrapper<Object> update(String id,InterestRateRq interestRateRq, String transactionId);

    DataResponseWrapper<Object> delete(String id, String transactionId);
}
