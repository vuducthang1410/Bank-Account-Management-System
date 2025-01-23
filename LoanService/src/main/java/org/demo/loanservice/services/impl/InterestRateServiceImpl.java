package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.common.MessageValue;
import org.demo.loanservice.common.Util;
import org.demo.loanservice.controllers.exception.InterestRateNotFoundException;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.dto.request.InterestRateRq;
import org.demo.loanservice.dto.response.InterestRateRp;
import org.demo.loanservice.entities.InterestRate;
import org.demo.loanservice.repositories.InterestRateRepository;
import org.demo.loanservice.services.IInterestRateService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class InterestRateServiceImpl implements IInterestRateService {
    private final InterestRateRepository interestRateRepository;
    private final Util util;

    @Override
    public DataResponseWrapper<Object> save(InterestRateRq interestRateRq, String transactionId) {
        InterestRate interestRate = new InterestRate();
        interestRate.setInterestRate(interestRateRq.getInterestRate());
        interestRate.setUnit(Unit.valueOf(interestRateRq.getUnit()));
        interestRate.setMinimumAmount(interestRateRq.getMinimumAmount());
        interestRate.setMinimumLoanTerm(interestRateRq.getMinimumLoanTerm());
        interestRate.setIsActive(false);
        interestRate.setIsDeleted(false);
        interestRate.setDateActive(DateUtil.getCurrentTimeUTC7());
        interestRateRepository.save(interestRate);
        return DataResponseWrapper.builder()
                .data(Map.of("InterestRateId", interestRate.getId()))
                .message(util.getMessageFromMessageSource(MessageValue.CREATED_SUCCESSFUL))
                .status("00000")
                .build();
    }

    @Override
    @Cacheable(value = "interest_rate",key = "#id",unless = "#result == null")
    public DataResponseWrapper<Object> getById(String id, String transactionId) {
        Optional<InterestRate> optionalInterestRate = interestRateRepository.findInterestRateByIdAndIsDeleted(id, false);
        if (optionalInterestRate.isEmpty())
            throw new InterestRateNotFoundException();
        return DataResponseWrapper.builder()
                .status("00000")
                .message(util.getMessageFromMessageSource(MessageValue.FIND_SUCCESSFULLY))
                .data(convertToInterestRateRp(optionalInterestRate.get()))
                .build();
    }

    @Override
    public DataResponseWrapper<Object> getAll(Integer pageNumber, Integer pageSize, String transactionId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate"));
        Page<InterestRate> interestRatePage = interestRateRepository.findAllByIsDeleted(false, pageable);
        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("totalRecord", interestRatePage.getTotalElements());
        List<InterestRateRp> interestRateRpList = interestRatePage.stream().map(this::convertToInterestRateRp).toList();
        dataResponse.put("interestRateList", interestRateRpList);
        return DataResponseWrapper.builder()
                .data(dataResponse)
                .message(util.getMessageFromMessageSource(MessageValue.FIND_SUCCESSFULLY))
                .status("00000")
                .build();
    }

    @Override
    @CacheEvict(value = "interest_rate",key = "#id")
    public DataResponseWrapper<Object> active(String id, String transactionId) {
        Optional<InterestRate> optionalInterestRate = interestRateRepository.findInterestRateByIdAndIsDeleted(id, false);
        if (optionalInterestRate.isEmpty())
            throw new InterestRateNotFoundException();
        InterestRate interestRate = optionalInterestRate.get();
        interestRate.setIsActive(!interestRate.getIsActive());
        interestRate.setDateActive(DateUtil.getCurrentTimeUTC7());
        interestRateRepository.save(interestRate);
        return DataResponseWrapper.builder()
                .status("00000")
                .message("Active interest rate successfully")
                .data(convertToInterestRateRp(interestRate))
                .build();
    }

    @Override
    @CacheEvict(value = "interest_rate",key = "#id")
    public DataResponseWrapper<Object> update(String id, InterestRateRq interestRateRq, String transactionId) {
        Optional<InterestRate> optionalInterestRate = interestRateRepository.findInterestRateByIdAndIsDeleted(id, false);
        if (optionalInterestRate.isEmpty())
            throw new InterestRateNotFoundException();
        InterestRate interestRate = optionalInterestRate.get();
        interestRate.setUnit(Unit.valueOf(interestRateRq.getUnit()));
        interestRate.setInterestRate(interestRateRq.getInterestRate());
        interestRate.setMinimumAmount(interestRateRq.getMinimumAmount());
        interestRate.setMinimumLoanTerm(interestRateRq.getMinimumLoanTerm());
        interestRate.setIsActive(false);
        interestRateRepository.save(interestRate);
        return DataResponseWrapper.builder()
                .status("00000")
                .message("Active interest rate successfully")
                .data(convertToInterestRateRp(interestRate))
                .build();
    }

    @Override
    @CacheEvict(value = "interest_rate",key = "#id")
    public DataResponseWrapper<Object> delete(String id, String transactionId) {
        Optional<InterestRate> optionalInterestRate = interestRateRepository.findInterestRateByIdAndIsDeleted(id, false);
        if (optionalInterestRate.isEmpty())
            throw new InterestRateNotFoundException();
        InterestRate interestRate = optionalInterestRate.get();
        interestRate.setIsDeleted(true);
        interestRateRepository.save(interestRate);
        return DataResponseWrapper.builder()
                .status("00000")
                .message("delete successfully")
                .data(interestRate.getId())
                .build();
    }

    private InterestRateRp convertToInterestRateRp(InterestRate interestRate) {
        InterestRateRp interestRateRp = new InterestRateRp();
        interestRateRp.setId(interestRate.getId());
        interestRateRp.setInterestRate(interestRate.getInterestRate().stripTrailingZeros().toPlainString());
        interestRateRp.setUnit(interestRate.getUnit().name());
        interestRateRp.setIsActive(interestRate.getIsActive().toString());
        interestRateRp.setMinimumAmount(interestRate.getMinimumAmount().stripTrailingZeros().toPlainString());
        interestRateRp.setMinimumLoanTerm(interestRate.getMinimumLoanTerm().toString());
        interestRateRp.setDateActive(DateUtil.format(DateUtil.DD_MM_YYY_HH_MM_SLASH,new Date(interestRate.getDateActive().getTime())));
        interestRateRp.setCreatedDate(DateUtil.format(DateUtil.DD_MM_YYY_HH_MM_SLASH, Date.from(interestRate.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant())));
        return interestRateRp;
    }
}
