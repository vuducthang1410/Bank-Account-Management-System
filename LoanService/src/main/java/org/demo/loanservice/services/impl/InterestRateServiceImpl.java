package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.common.MessageValue;
import org.demo.loanservice.common.Util;
import org.demo.loanservice.controllers.exception.DataNotFoundException;
import org.demo.loanservice.dto.MapToDto;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.dto.request.InterestRateRq;
import org.demo.loanservice.dto.response.InterestRateRp;
import org.demo.loanservice.entities.InterestRate;
import org.demo.loanservice.entities.LoanProduct;
import org.demo.loanservice.repositories.InterestRateRepository;
import org.demo.loanservice.repositories.LoanProductRepository;
import org.demo.loanservice.services.IInterestRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class InterestRateServiceImpl implements IInterestRateService {
    private final InterestRateRepository interestRateRepository;
    private final LoanProductRepository loanProductRepository;
    private final Util util;
    private final Logger log = LoggerFactory.getLogger(InterestRateServiceImpl.class);

    @Override
    public DataResponseWrapper<Object> save(InterestRateRq interestRateRq, String transactionId) {
        Optional<LoanProduct> loanProductOptional = loanProductRepository.findByIdAndIsDeleted(interestRateRq.getLoanProductId(), Boolean.FALSE);
        if (loanProductOptional.isEmpty()) {
            log.info(MessageData.MESSAGE_LOG, MessageData.LOAN_PRODUCT_NOT_FOUNT.getMessageLog(), transactionId);
            throw new DataNotFoundException(MessageData.LOAN_PRODUCT_NOT_FOUNT.getKeyMessage(), MessageData.LOAN_PRODUCT_NOT_FOUNT.getCode());
        }
        LoanProduct loanProduct = loanProductOptional.get();
        InterestRate interestRate = new InterestRate();
        interestRate.setInterestRate(interestRateRq.getInterestRate());
        interestRate.setUnit(Unit.valueOf(interestRateRq.getUnit()));
        interestRate.setMinimumAmount(interestRateRq.getMinimumAmount());
        interestRate.setMinimumLoanTerm(interestRateRq.getMinimumLoanTerm());
        interestRate.setIsActive(false);
        interestRate.setIsDeleted(false);
        interestRate.setDateActive(DateUtil.getCurrentTimeUTC7());
        interestRate.setLoanProduct(loanProduct);
        interestRateRepository.save(interestRate);
        return DataResponseWrapper.builder()
                .data(Map.of("InterestRateId", interestRate.getId()))
                .message(util.getMessageFromMessageSource(MessageValue.CREATED_SUCCESSFUL))
                .status("00000")
                .build();
    }

    @Override
    @Cacheable(value = "interest_rate", key = "#id", unless = "#result == null")
    public DataResponseWrapper<Object> getById(String id, String transactionId) {
        Optional<InterestRate> optionalInterestRate = interestRateRepository.findInterestRateByIdAndIsDeleted(id, false);
        if (optionalInterestRate.isEmpty()) {
            log.info(MessageData.MESSAGE_LOG, MessageData.INTEREST_RATE_NOT_FOUND.getMessageLog(), transactionId);
            throw new DataNotFoundException(MessageData.INTEREST_RATE_NOT_FOUND.getKeyMessage(), MessageData.INTEREST_RATE_NOT_FOUND.getCode());
        }
        return DataResponseWrapper.builder()
                .status("00000")
                .message(util.getMessageFromMessageSource(MessageValue.FIND_SUCCESSFULLY))
                .data(MapToDto.convertToInterestRateRp(optionalInterestRate.get()))
                .build();
    }

    @Override
    public DataResponseWrapper<Object> getAll(Integer pageNumber, Integer pageSize, String transactionId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate"));
        Page<InterestRate> interestRatePage = interestRateRepository.findAllByIsDeleted(false, pageable);
        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("totalRecord", interestRatePage.getTotalElements());
        List<InterestRateRp> interestRateRpList = interestRatePage.stream().map(
                MapToDto::convertToInterestRateRp
        ).toList();
        dataResponse.put("interestRateList", interestRateRpList);
        return DataResponseWrapper.builder()
                .data(dataResponse)
                .message(util.getMessageFromMessageSource(MessageValue.FIND_SUCCESSFULLY))
                .status("00000")
                .build();
    }

    @Override
    @CacheEvict(value = "interest_rate", key = "#id")
    public DataResponseWrapper<Object> active(String id, String transactionId) {
        InterestRate interestRate = getInterestRateById(id, transactionId);
        interestRate.setIsActive(!interestRate.getIsActive());
        interestRate.setDateActive(DateUtil.getCurrentTimeUTC7());
        interestRateRepository.save(interestRate);
        return DataResponseWrapper.builder()
                .status("00000")
                .message("Active interest rate successfully")
                .data(MapToDto.convertToInterestRateRp(interestRate))
                .build();
    }

    @Override
    @CacheEvict(value = "interest_rate", key = "#id")
    public DataResponseWrapper<Object> update(String id, InterestRateRq interestRateRq, String transactionId) {
        InterestRate interestRate = getInterestRateById(id, transactionId);
        interestRate.setUnit(Unit.valueOf(interestRateRq.getUnit()));
        interestRate.setInterestRate(interestRateRq.getInterestRate());
        interestRate.setMinimumAmount(interestRateRq.getMinimumAmount());
        interestRate.setMinimumLoanTerm(interestRateRq.getMinimumLoanTerm());
        interestRate.setIsActive(false);
        interestRateRepository.save(interestRate);
        return DataResponseWrapper.builder()
                .status("00000")
                .message("Active interest rate successfully")
                .data(MapToDto.convertToInterestRateRp(interestRate))
                .build();
    }

    @Override
    @CacheEvict(value = "interest_rate", key = "#id")
    public DataResponseWrapper<Object> delete(String id, String transactionId) {
        InterestRate interestRate = getInterestRateById(id, transactionId);
        interestRate.setIsDeleted(true);
        interestRateRepository.save(interestRate);
        return DataResponseWrapper.builder()
                .status("00000")
                .message("delete successfully")
                .data(interestRate.getId())
                .build();
    }

    @Override
    public InterestRate getInterestRateById(String id, String transactionId) {
        Optional<InterestRate> optionalInterestRate = interestRateRepository.findInterestRateByIdAndIsDeleted(id, false);
        if (optionalInterestRate.isEmpty()) {
            log.info(MessageData.MESSAGE_LOG, MessageData.INTEREST_RATE_NOT_FOUND.getMessageLog(), transactionId);
            throw new DataNotFoundException(MessageData.INTEREST_RATE_NOT_FOUND.getKeyMessage(), MessageData.INTEREST_RATE_NOT_FOUND.getCode());
        }
        return optionalInterestRate.get();
    }
    @Override
    public InterestRate getInterestRateByLoanAmount(BigDecimal loanAmount, String transactionId){
        Optional<InterestRate> optionalInterestRate=interestRateRepository.findFirstByMinimumAmountLessThanEqualOrderByMinimumAmount(loanAmount,false);
        if(optionalInterestRate.isEmpty()){
            log.info(MessageData.MESSAGE_LOG, MessageData.INTEREST_RATE_NOT_FOUND.getMessageLog(), transactionId);
            throw new DataNotFoundException(MessageData.INTEREST_RATE_NOT_FOUND.getKeyMessage(), MessageData.INTEREST_RATE_NOT_FOUND.getCode());
        }
        return optionalInterestRate.get();
    }
}
