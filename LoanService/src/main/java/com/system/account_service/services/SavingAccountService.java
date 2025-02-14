package com.system.account_service.services;

import com.system.account_service.dtos.response.PageDataDTO;
import com.system.account_service.dtos.saving.CreateSavingDTO;
import com.system.account_service.dtos.saving.SavingRp;
import com.system.account_service.entities.SavingAccount;

import java.math.BigDecimal;

public interface SavingAccountService {
    SavingRp create(CreateSavingDTO data);

    SavingRp updateBalance(String id, BigDecimal balance);

    void delete(String id);

    SavingRp findById(String id);

    PageDataDTO<SavingRp> findAll(Integer page, Integer pageSize);

    SavingAccount getDataId(String id);
}
