package com.system.account_service.services;

import com.system.account_service.dtos.interest_rate.InterestRateDTO;
import com.system.account_service.dtos.response.PageDataDTO;
import com.system.account_service.entities.InterestRates;

public interface InterestRateService {
    InterestRates create(InterestRateDTO data);

    InterestRates update(String id, InterestRateDTO data);

    void delete(String id);

    InterestRates findById(String id);

    PageDataDTO<InterestRates> findAll(Integer page, Integer pageSize);
}
