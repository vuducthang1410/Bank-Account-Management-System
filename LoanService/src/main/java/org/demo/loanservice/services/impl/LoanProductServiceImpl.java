package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.controllers.exception.DataNotFoundException;
import org.demo.loanservice.dto.MapEntityToDto;
import org.demo.loanservice.dto.enumDto.ApplicableObjects;
import org.demo.loanservice.dto.enumDto.LoanType;
import org.demo.loanservice.dto.request.LoanProductRq;
import org.demo.loanservice.dto.response.InterestRateRp;
import org.demo.loanservice.dto.response.LoanProductRp;
import org.demo.loanservice.entities.InterestRate;
import org.demo.loanservice.entities.LoanProduct;
import org.demo.loanservice.repositories.LoanProductRepository;
import org.demo.loanservice.services.IInterestRateService;
import org.demo.loanservice.services.ILoanProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanProductServiceImpl implements ILoanProductService {
    private final LoanProductRepository loanProductRepository;
    private final IInterestRateService interestRateService;
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
    public DataResponseWrapper<Object> getById(String id, String transactionId) {
        LoanProduct loanProduct = getLoanProductById(id, transactionId);
        List<InterestRate> interestRateList=interestRateService.interestRateList(List.of(loanProduct.getId()));
        LoanProductRp loanProductRp = convertToLoanProductRp(loanProduct, interestRateList);
        return DataResponseWrapper.builder()
                .data(loanProductRp)
                .message("successfully")
                .status("200")
                .build();
    }

    @Override
    public DataResponseWrapper<Object> getAll(Integer pageNumber, Integer pageSize, String transactionId) {
        // Create a Pageable object with sorting by createdDate in descending order
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());

        // Retrieve a paginated list of LoanProduct entities that are not deleted
        Page<LoanProduct> loanProductPage = loanProductRepository.findAllByIsDeleted(false, pageable);
        List<LoanProduct> loanProductList = loanProductPage.getContent();
        log.info("Fetched LoanProduct list: {} records", loanProductList.size());

        // Extract the IDs of the retrieved LoanProduct entities
        List<String> loanProductIdList = loanProductList.stream().map(LoanProduct::getId).toList();
        log.debug("LoanProduct ID list: {}", loanProductIdList);

        // Retrieve the list of InterestRate entities associated with the LoanProduct IDs
        List<InterestRate> interestRateList = interestRateService.interestRateList(loanProductIdList);
        log.info("Fetched InterestRate list: {} records", interestRateList.size());

        // Group InterestRate entities by LoanProduct ID
        Map<String, List<InterestRate>> interestRateMap = interestRateList.stream()
                .collect(Collectors.groupingBy(ir -> ir.getLoanProduct().getId()));
        log.debug("InterestRate map created with {} entries", interestRateMap.size());

        // Prepare response data
        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("totalRecords", loanProductPage.getTotalElements());

        // Convert LoanProduct entities to LoanProductRp response objects
        List<LoanProductRp> loanProductRpList = loanProductList
                .stream()
                .map(loanProduct -> {
                    List<InterestRate> interestRateListById = interestRateMap.getOrDefault(loanProduct.getId(), new ArrayList<>());
                    return convertToLoanProductRp(loanProduct, interestRateListById);
                }).toList();
        dataResponse.put("loanProductRpList", loanProductRpList);

        log.info("Successfully processed {} LoanProductRp records", loanProductRpList.size());

        // Return the wrapped response
        return DataResponseWrapper.builder()
                .data(dataResponse)
                .message("Successfully retrieved loan products")
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
    public DataResponseWrapper<Object> delete(String id, String transactionId) {
        LoanProduct loanProduct = getLoanProductById(id, transactionId);
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

    /**
     * Converts a LoanProduct entity to a LoanProductRp DTO.
     *
     * @param loanProduct      The LoanProduct entity to be converted.
     * @param interestRateList list containing lists of InterestRate entities of loan product
     * @return A LoanProductRp object representing the converted data.
     */
    public LoanProductRp convertToLoanProductRp(LoanProduct loanProduct, List<InterestRate> interestRateList) {
        LoanProductRp loanProductRp = new LoanProductRp();
        loanProductRp.setProductId(loanProduct.getId());
        loanProductRp.setProductName(loanProduct.getNameProduct());
        // Convert and set product description if it exists
        if (loanProduct.getDescription() != null) {
            loanProductRp.setProductDescription(new String(loanProduct.getDescription(), StandardCharsets.UTF_8));
        }
        loanProductRp.setApplicableObjects(loanProduct.getApplicableObjects().name());
        loanProductRp.setFormLoan(loanProduct.getFormLoan().name());
        loanProductRp.setLoanLimit(loanProduct.getLoanLimit().toPlainString());

        // Retrieve and convert interest rate data if the map is provided
        log.debug("interest rate list: {}", interestRateList);
        if (interestRateList != null && !interestRateList.isEmpty()) {
            log.debug("Set {} InterestRate records for LoanProduct ID: {}", interestRateList.size(), loanProduct.getId());
            // Convert InterestRate entities to DTOs
            List<InterestRateRp> interestRateRpList = interestRateList.stream()
                    .map(MapEntityToDto::convertToInterestRateRp)
                    .toList();
            loanProductRp.setInterestRate(interestRateRpList);
        }else {
            loanProductRp.setInterestRate(List.of());
        }
        loanProductRp.setTermLimit(loanProduct.getTermLimit());
        if (loanProduct.getUtilities() != null) {
            loanProductRp.setUtilities(new String(loanProduct.getUtilities(), StandardCharsets.UTF_8));
        }
        loanProductRp.setProductUrlImage(loanProduct.getProductUrlImage());
        if (loanProduct.getLoanCondition() != null) {
            loanProductRp.setLoanCondition(new String(loanProduct.getLoanCondition(), StandardCharsets.UTF_8));
        }
        // Format and set the created date as a string
        loanProductRp.setCreatedDate(DateUtil.format(DateUtil.YYYY_MM_DD_HH_MM_SS, loanProduct.getCreatedDate()));
        return loanProductRp;
    }

}
