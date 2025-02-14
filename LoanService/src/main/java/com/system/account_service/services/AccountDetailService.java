package com.system.account_service.services;

import com.system.account_service.dtos.account_detail.CreateAccountDetailDTO;
import com.system.account_service.dtos.response.PageDataDTO;
import com.system.account_service.entities.AccountDetails;

public interface AccountDetailService {
    AccountDetails create(CreateAccountDetailDTO data);

    AccountDetails updateStatus(String id, String status);

    void delete(String id);

    AccountDetails findById(String id);

    PageDataDTO<AccountDetails> findAll(Integer page, Integer pageSize);
}
