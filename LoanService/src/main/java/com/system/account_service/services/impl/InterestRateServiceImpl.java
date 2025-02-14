package com.system.account_service.services.impl;

import com.system.account_service.dtos.interest_rate.InterestRateDTO;
import com.system.account_service.dtos.response.PageDataDTO;
import com.system.account_service.entities.InterestRates;
import com.system.account_service.entities.type.Unit;
import com.system.account_service.exception.payload.ResourceNotFoundException;
import com.system.account_service.repositories.InterestRateRepository;
import com.system.account_service.services.InterestRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestRateServiceImpl implements InterestRateService {
    private final InterestRateRepository repository;

    @Override
    public InterestRates create(InterestRateDTO data) {
        InterestRates interestRate = InterestRates.builder()
                .rate(data.getRate())
                .unit(Unit.valueOf(data.getUnit()))
                .isActive(data.getIsActive())
                .build();

        return repository.save(interestRate);
    }

    @Override
//    @CachePut(value = "interest_rate", key = "#id")
    public InterestRates update(String id, InterestRateDTO data) {
        InterestRates interestRate = repository.findByInterestRateIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        interestRate.setRate(data.getRate());
        interestRate.setUnit(Unit.valueOf(data.getUnit()));
        interestRate.setIsActive(data.getIsActive());

        return repository.save(interestRate);
    }

    @Override
//    @CacheEvict(value = "interest_rate", key = "#id")
    public void delete(String id) {
        InterestRates data = repository.findByInterestRateIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        data.setDeleted(true);
        repository.save(data);
    }

    @Override
//    @Cacheable(value = "interest_rate", key = "#id", unless = "#result == null")
    public InterestRates findById(String id) {
        return repository.findByInterestRateIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public PageDataDTO<InterestRates> findAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by("createdAt"));
        Page<InterestRates> pageData = repository.findAllByDeleted(false, pageable);
        List<InterestRates> listData = pageData.stream().toList();

        return PageDataDTO.<InterestRates> builder()
                .total(pageData.getTotalElements())
                .listData(listData)
                .build();
    }
}
