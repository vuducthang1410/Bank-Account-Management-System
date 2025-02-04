package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.controllers.exception.InterestRateNotFoundException;
import org.demo.loanservice.dto.enumDto.ApplicableObjects;
import org.demo.loanservice.dto.enumDto.LoanType;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.dto.request.LoanProductRq;
import org.demo.loanservice.dto.response.LoanProductRp;
import org.demo.loanservice.dto.response.LoanTermRp;
import org.demo.loanservice.entities.InterestRate;
import org.demo.loanservice.entities.LoanProduct;
import org.demo.loanservice.entities.LoanTerm;
import org.demo.loanservice.repositories.InterestRateRepository;
import org.demo.loanservice.repositories.LoanProductRepository;
import org.demo.loanservice.repositories.LoanTermRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanProductServiceImpl implements ILoanProductService {
    private final LoanProductRepository loanProductRepository;
    private final InterestRateRepository interestRateRepository;
    private final LoanTermRepository loanTermRepository;

    @Override
    @Transactional
    public DataResponseWrapper<Object> save(LoanProductRq loanProductRq, String transactionId) {
        Optional<InterestRate> optionalInterestRate = interestRateRepository
                .findInterestRateByIdAndIsDeleted(loanProductRq.getInterestRateId(), false);
        if (optionalInterestRate.isEmpty()) {
            throw new InterestRateNotFoundException();
        }
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setLoanLimit(loanProductRq.getLoanLimit());
        loanProduct.setLoanCondition(loanProductRq.getLoanCondition().getBytes(StandardCharsets.UTF_8));
        loanProduct.setNameProduct(loanProductRq.getNameLoanProduct());
        loanProduct.setDescription(loanProductRq.getDescription().getBytes(StandardCharsets.UTF_8));
        loanProduct.setApplicableObjects(ApplicableObjects.valueOf(loanProductRq.getApplicableObjects()));
        loanProduct.setInterestRate(optionalInterestRate.get());
        loanProduct.setFormLoan(LoanType.valueOf(loanProductRq.getLoanForm()));
        loanProduct.setProductUrlImage("https://example.com/djaiajd.jpg");//todo
        loanProduct.setUtilities(loanProductRq.getUtilities().getBytes(StandardCharsets.UTF_8));
        loanProduct.setIsDeleted(false);
        loanProductRepository.save(loanProduct);
        List<LoanTerm> loanTermList=loanProductRq.getLoanTermRqList()
                .stream()
                .map(e->new LoanTerm(loanProduct,e.getTerm(), Unit.valueOf(e.getUnit())))
                .toList();
        loanTermRepository.saveAll(loanTermList);
        return DataResponseWrapper.builder()
                .data(loanProduct.getId())
                .message("successfully")
                .status("200")
                .build();
    }

    @Override
    @Cacheable(value = "loan-product", key = "#id", unless = "#result == null")
    public DataResponseWrapper<Object> getById(String id, String transactionId) {
        Optional<LoanProduct> optionalLoanProduct = loanProductRepository.findByIdAndIsDeleted(id, false);
        if (optionalLoanProduct.isEmpty()) {
            throw new InterestRateNotFoundException();
        }
        LoanProductRp loanProductRp = convertToLoanProductRp(optionalLoanProduct.get());
        return DataResponseWrapper.builder()
                .data(loanProductRp)
                .message("successfully")
                .status("200")
                .build();
    }

    @Override
    public DataResponseWrapper<Object> getAll(Integer pageNumber, Integer pageSize, String transactionId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        Page<LoanProduct> loanProductPage = loanProductRepository.findAllByIsDeleted(false,pageable);
        Map<String,Object> dataResponse = new HashMap<>();
        dataResponse.put("totalRecords", loanProductPage.getTotalElements());
        List<LoanProductRp> loanProductRpList=loanProductPage.getContent()
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
        Optional<LoanProduct> optionalLoanProduct = loanProductRepository.findByIdAndIsDeleted(id, false);
        if (optionalLoanProduct.isEmpty()) {
            throw new InterestRateNotFoundException();
        }
        LoanProduct loanProduct = optionalLoanProduct.get();
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
        loanProductRp.setInterestRate(loanProduct.getInterestRate().getInterestRate().toPlainString());
        loanProductRp.setInterestRateUnit(loanProduct.getInterestRate().getUnit().name());
        loanProductRp.setLoanTerm(loanProduct
                .getLoanTermList()
                .stream()
                .map(this::converToLoanTermRp)
                .collect(Collectors.toList()));
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

    private LoanTermRp converToLoanTermRp(LoanTerm loanTerm) {
        LoanTermRp loanTermRp = new LoanTermRp();
        loanTermRp.setLoanTermId(loanTerm.getId());
        loanTermRp.setProductId(loanTerm.getLoanProduct().getId());
        loanTermRp.setTerm(loanTerm.getTerm());
        loanTermRp.setUnit(loanTerm.getUnit().name());
        return loanTermRp;
    }
}
