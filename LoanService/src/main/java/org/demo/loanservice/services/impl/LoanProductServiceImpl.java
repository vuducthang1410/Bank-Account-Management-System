package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.controllers.exception.DataNotFoundException;
import org.demo.loanservice.dto.MapToDto;
import org.demo.loanservice.dto.enumDto.ApplicableObjects;
import org.demo.loanservice.dto.enumDto.LoanType;
import org.demo.loanservice.dto.request.LoanProductRq;
import org.demo.loanservice.dto.response.InterestRateRp;
import org.demo.loanservice.dto.response.LoanProductRp;
import org.demo.loanservice.entities.LoanProduct;
import org.demo.loanservice.repositories.LoanProductRepository;
import org.demo.loanservice.services.ILoanProductService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanProductServiceImpl implements ILoanProductService {
    private final LoanProductRepository loanProductRepository;
    private final Logger log = LogManager.getLogger(LoanProductServiceImpl.class);

    @Override
    @Transactional
    public DataResponseWrapper<Object> save(LoanProductRq loanProductRq, String transactionId) {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setLoanLimit(loanProductRq.getLoanLimit());
        loanProduct.setLoanCondition(loanProductRq.getLoanCondition().getBytes(StandardCharsets.UTF_8));
        loanProduct.setNameProduct(loanProductRq.getNameLoanProduct());
        loanProduct.setDescription(loanProductRq.getDescription().getBytes(StandardCharsets.UTF_8));
        loanProduct.setApplicableObjects(ApplicableObjects.valueOf(loanProductRq.getApplicableObjects()));
        loanProduct.setFormLoan(LoanType.valueOf(loanProductRq.getLoanForm()));
        loanProduct.setProductUrlImage("https://example.com/djaiajd.jpg");//todo
        loanProduct.setUtilities(loanProductRq.getUtilities().getBytes(StandardCharsets.UTF_8));
        loanProduct.setIsDeleted(false);
        loanProduct.setTermLimit(loanProductRq.getTermLimit());
        loanProductRepository.save(loanProduct);
        return DataResponseWrapper.builder()
                .data(loanProduct.getId())
                .message("successfully")
                .status("200")
                .build();
    }

    @Override
    @Cacheable(value = "loan-product", key = "#id", unless = "#result == null")
    public DataResponseWrapper<Object> getById(String id, String transactionId) {
        LoanProduct loanProduct=getLoanProductById(id,transactionId);
        LoanProductRp loanProductRp = convertToLoanProductRp(loanProduct);
        return DataResponseWrapper.builder()
                .data(loanProductRp)
                .message("successfully")
                .status("200")
                .build();
    }

    @Override
    public DataResponseWrapper<Object> getAll(Integer pageNumber, Integer pageSize, String transactionId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        Page<LoanProduct> loanProductPage = loanProductRepository.findAllByIsDeleted(false, pageable);
        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("totalRecords", loanProductPage.getTotalElements());
        List<LoanProductRp> loanProductRpList = loanProductPage.getContent()
                .stream()
                .map(this::convertToLoanProductRp)
                .toList();
        dataResponse.put("loanProductRpList", loanProductRpList);
        return DataResponseWrapper.builder()
                .data(dataResponse)
                .message("successfully")
                .status("200")
                .build();
    }

    @Override
    public DataResponseWrapper<Object> active(String id, String transactionId) {
        return null;
    }

    @Override
    public DataResponseWrapper<Object> update(String id, LoanProductRq loanProductRq, String transactionId) {
        return null;
    }

    @Override
    @CacheEvict(value = "loan-product", key = "#id")
    public DataResponseWrapper<Object> delete(String id, String transactionId) {
        LoanProduct loanProduct = getLoanProductById(id,transactionId);
        loanProduct.setIsDeleted(true);
        loanProductRepository.save(loanProduct);
        return DataResponseWrapper.builder()
                .data(loanProduct.getId())
                .message("successfully")
                .status("200")
                .build();
    }

    @Override
    public DataResponseWrapper<Object> saveImageLoanProduct(String id, MultipartFile image, String transactionId) {
        return null;
    }

    @Override
    public LoanProduct getLoanProductById(String id, String transactionId) {
        Optional<LoanProduct> optionalLoanProduct = loanProductRepository.findByIdAndIsDeleted(id, false);
        if (optionalLoanProduct.isEmpty()) {
            log.info(MessageData.MESSAGE_LOG, MessageData.LOAN_PRODUCT_NOT_FOUNT.getMessageLog(), transactionId);
            throw new DataNotFoundException(MessageData.LOAN_PRODUCT_NOT_FOUNT.getKeyMessage(), MessageData.LOAN_PRODUCT_NOT_FOUNT.getCode());
        }
        return optionalLoanProduct.get();
    }

    public LoanProductRp convertToLoanProductRp(LoanProduct loanProduct) {
        LoanProductRp loanProductRp = new LoanProductRp();
        loanProductRp.setProductId(loanProduct.getId());
        loanProductRp.setProductName(loanProduct.getNameProduct());
        if (loanProduct.getDescription() != null) {
            loanProductRp.setProductDescription(new String(loanProduct.getDescription(), StandardCharsets.UTF_8));
        }
        loanProductRp.setApplicableObjects(loanProduct.getApplicableObjects().name());
        loanProductRp.setFormLoan(loanProduct.getFormLoan().name());
        loanProductRp.setLoanLimit(loanProduct.getLoanLimit().toPlainString());
        List<InterestRateRp> interestRateRpList = loanProduct.getInterestRateSet().stream().map(MapToDto::convertToInterestRateRp).toList();
        loanProductRp.setInterestRate(interestRateRpList);
        loanProductRp.setTermLimit(loanProduct.getTermLimit());
        if (loanProduct.getUtilities() != null) {
            loanProductRp.setUtilities(new String(loanProduct.getUtilities(), StandardCharsets.UTF_8));
        }
        loanProductRp.setProductUrlImage(loanProduct.getProductUrlImage());
        if (loanProduct.getLoanCondition() != null) {
            loanProductRp.setLoanCondition(new String(loanProduct.getLoanCondition(), StandardCharsets.UTF_8));
        }
        loanProductRp.setCreatedDate(DateUtil.format(DateUtil.YYYY_MM_DD_HH_MM_SS, loanProduct.getCreatedDate()));
        return loanProductRp;
    }
}
