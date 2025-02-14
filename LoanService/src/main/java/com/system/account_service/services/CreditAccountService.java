package com.system.account_service.services;

import com.system.account_service.dtos.credit.CreateCreditDTO;
import com.system.account_service.dtos.credit.CreditRp;
import com.system.account_service.dtos.response.PageDataDTO;
import com.system.account_service.entities.CreditAccount;

import java.math.BigDecimal;

public interface CreditAccountService {
    CreditRp create(CreateCreditDTO data);

    CreditRp updateDebtBalance(String id, BigDecimal debtBalance);

    void delete(String id);

    CreditRp findById(String id);

    PageDataDTO<CreditRp> findAll(Integer page, Integer pageSize);

    CreditAccount getDataId(String id);
}
