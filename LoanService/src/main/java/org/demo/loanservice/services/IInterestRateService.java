package org.demo.loanservice.services;

import org.demo.loanservice.dto.request.InterestRateRq;
import org.demo.loanservice.entities.InterestRate;

import java.math.BigDecimal;

public interface IInterestRateService extends IBaseService<InterestRateRq>{
    InterestRate getInterestRateById(String id, String transactionId);

    InterestRate getInterestRateByLoanAmount(BigDecimal loanAmount, String transactionId);
}
