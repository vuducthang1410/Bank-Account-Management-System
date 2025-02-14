package com.system.account_service.services;

import com.system.account_service.dtos.banking.BankingRp;
import com.system.account_service.dtos.banking.CreateBankingDTO;
import com.system.account_service.dtos.response.PageDataDTO;
import com.system.account_service.entities.BankingAccount;

import java.math.BigDecimal;

public interface BankingAccountService {
    BankingRp create(CreateBankingDTO data);

    BankingRp updateBalance(String id, BigDecimal balance);

    void delete(String id);

    BankingRp findById(String id);

    PageDataDTO<BankingRp> findAll(Integer page, Integer pageSize);

    BankingAccount getDataId(String id);
}
