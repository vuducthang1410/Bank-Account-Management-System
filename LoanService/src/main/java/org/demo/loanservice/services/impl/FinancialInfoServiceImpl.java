package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.common.Util;
import org.demo.loanservice.controllers.exception.DataNotFoundException;
import org.demo.loanservice.dto.CICResponse;
import org.demo.loanservice.dto.enumDto.ApplicableObjects;
import org.demo.loanservice.dto.enumDto.DocumentType;
import org.demo.loanservice.dto.enumDto.RequestStatus;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.dto.request.ApproveFinancialInfoRq;
import org.demo.loanservice.dto.request.FinancialInfoRq;
import org.demo.loanservice.dto.response.FinancialInfoRp;
import org.demo.loanservice.dto.response.LegalDocumentRp;
import org.demo.loanservice.entities.FinancialInfo;
import org.demo.loanservice.entities.LegalDocuments;
import org.demo.loanservice.repositories.FinancialInfoRepository;
import org.demo.loanservice.repositories.InterestRateRepository;
import org.demo.loanservice.repositories.LegalDocumentsRepository;
import org.demo.loanservice.repositories.LoanDetailInfoRepository;
import org.demo.loanservice.services.IFinancialInfoService;
import org.demo.loanservice.wiremockService.CICService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FinancialInfoServiceImpl implements IFinancialInfoService {
    private final InterestRateRepository interestRateRepository;
    private final FinancialInfoRepository financialInfoRepository;
    private final LoanDetailInfoRepository loanDetailInfoRepository;
    private final LegalDocumentsRepository legalDocumentsRepository;
    private final CICService cicService;
    private final Util util;

    @Override
    @Transactional
    public DataResponseWrapper<Object> saveInfoIndividualCustomer(
            FinancialInfoRq financialInfoRq,
            List<MultipartFile> incomeVerificationDocuments,
            String transactionId) {

        CICResponse cicResponse = cicService.getCreditScore("079123456789", "vu duc thang", "", "0123456789");
        FinancialInfo financialInfo = new FinancialInfo();
        financialInfo.setCifCode("123456789");//todo
        financialInfo.setIncome(financialInfoRq.getIncome().stripTrailingZeros().toPlainString());
        financialInfo.setUnit(Unit.valueOf(financialInfoRq.getUnit()));
        financialInfo.setIncomeSource(financialInfoRq.getIncomeSource());
        financialInfo.setIncomeType(financialInfoRq.getIncomeType());
        financialInfo.setRequestStatus(RequestStatus.PENDING);
        financialInfo.setCreditScore(cicResponse.getCreditScore());
        financialInfo.setDebtStatus(cicResponse.getDebtStatus());
        financialInfo.setIsExpired(false);
        financialInfo.setExpiredDate(new Date(DateUtil.getDateOfAfterNMonth(6).getTime()));
        financialInfo.setIsDeleted(false);
        financialInfo.setLastUpdatedCreditReview(DateUtil.convertStringToTimeStamp(cicResponse.getLastUpdated()));
        financialInfo.setApplicableObjects(ApplicableObjects.INDIVIDUAL_CUSTOMER);
        financialInfoRepository.save(financialInfo);

        List<LegalDocuments> legalDocumentsList = new ArrayList<>();
        incomeVerificationDocuments.forEach(multipartFile -> {
            LegalDocuments legalDocument = new LegalDocuments();
            legalDocument.setCifCode("123456789");//todo: call to account service get info
            legalDocument.setDescription("Tài liệu về thông tin tài chính");
            legalDocument.setIsDeleted(false);
            legalDocument.setExpirationDate(new Date(DateUtil.getDateOfAfterNMonth(3).getTime()));
            legalDocument.setDocumentType(DocumentType.LOAN_DOCUMENT);
            legalDocument.setUrlDocument(multipartFile.getOriginalFilename());//todo: save file to S3 and get url
            legalDocument.setRequestStatus(RequestStatus.APPROVED);
            legalDocumentsList.add(legalDocument);
        });
        legalDocumentsRepository.saveAll(legalDocumentsList);

        legalDocumentsList.forEach(legalDocument ->
                legalDocumentsRepository.insertFinancialInfoDocument(
                        financialInfo.getId(),
                        legalDocument.getId())
        );
        return DataResponseWrapper.builder()
                .data(financialInfo.getId())
                .status("00000")
                .message("Thông tin tài chính của bạn được đăng ký thành công và đang chờ xét duyệt!!!")//todo: handler response message
                .build();
    }

    @Override
    public DataResponseWrapper<Object> getAllInfoIsByStatus(
            Integer pageNumber,
            Integer pageSize,
            String status,
            String transactionId) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<FinancialInfo> financialInfoPage = financialInfoRepository.findAllByIsDeletedAndRequestStatus(false, RequestStatus.valueOf(status), page);
        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("totalRecords", financialInfoPage.getTotalElements());
        List<FinancialInfoRp> financialInfoRpList = financialInfoPage.getContent()
                .stream()
                .map(financialInfo -> convertToFinancialInfoRp(financialInfo, false))
                .toList();
        dataResponse.put("financialInfoRpList", financialInfoRpList);
        return DataResponseWrapper.builder()
                .data(dataResponse)
                .status("00000")
                .message("")//todo: handler response message
                .build();
    }

    @Override
    @Cacheable(value = "financial-info", key = "#id", unless = "#result == null")
    public DataResponseWrapper<Object> getDetailInfoById(String id, String transactionId) {
        Optional<FinancialInfo> financialInfoOptional = financialInfoRepository.findByIdAndIsDeleted(id, false);
        if (financialInfoOptional.isEmpty()) {
            throw new DataNotFoundException("","");//todo: handle exception
        }
        FinancialInfoRp financialInfoRp = convertToFinancialInfoRp(financialInfoOptional.get(), true);
        return DataResponseWrapper.builder()
                .data(financialInfoRp)
                .status("00000")
                .message("")//todo: handler response message
                .build();
    }

    @Override
    @CacheEvict(value = "financial-info", key = "#financialInfoRq.financialInfoId")
    public DataResponseWrapper<Object> approveFinancialInfo(ApproveFinancialInfoRq financialInfoRq, String transactionId) {
        Optional<FinancialInfo> financialInfoOptional = financialInfoRepository
                .findByIdAndIsDeleted(financialInfoRq.getFinancialInfoId(), false);
        if (financialInfoOptional.isEmpty()) {
            throw new DataNotFoundException("","");//todo: handle exception
        }
        FinancialInfo financialInfo = financialInfoOptional.get();
        financialInfo.setRequestStatus(RequestStatus.valueOf(financialInfoRq.getStatusFinancialInfo()));
        financialInfo.setNote(financialInfoRq.getNote());
        financialInfoRepository.save(financialInfo);
        //todo: call notification service

        return DataResponseWrapper.builder()
                .data(financialInfo.getId())
                .status("00000")
                .message("")//todo
                .build();
    }

    @Override
    public DataResponseWrapper<Object> verifyFinancialInfo(String transactionId) {
        //todo: get customer id form Security Context
        String customerId="123456789";
        Optional<FinancialInfo> financialInfoOptional = financialInfoRepository.findByIsDeletedAndCifCode(false, customerId);
        if (financialInfoOptional.isEmpty()) {
            throw new DataNotFoundException("","");
        }
        Boolean isApprove=financialInfoOptional.get().getRequestStatus().equals(RequestStatus.APPROVED);
        return DataResponseWrapper.builder()
                .message("")
                .data(isApprove)
                .build();
    }

    private FinancialInfoRp convertToFinancialInfoRp(FinancialInfo financialInfo, Boolean isDetail) {
        FinancialInfoRp financialInfoRp = new FinancialInfoRp();
        financialInfoRp.setCifCode(financialInfo.getCifCode());
        financialInfoRp.setFinancialInfoId(financialInfo.getId());
        financialInfoRp.setIncome(financialInfo.getIncome());
        financialInfoRp.setUnit(financialInfo.getUnit().toString());
        financialInfoRp.setCreditScore(financialInfo.getCreditScore().toString());
        financialInfoRp.setIncomeSource(financialInfo.getIncomeSource());
        financialInfoRp.setIncomeType(financialInfo.getIncomeType());
        financialInfoRp.setDebtStatus(financialInfo.getDebtStatus());
        List<LegalDocumentRp> legalDocumentsRpList = new ArrayList<>();
        if (!isDetail) {
            financialInfoRp.setCountLegalDocument(financialInfo.getFinancialInfoDocumentSet().size());
        } else {
            financialInfo.getFinancialInfoDocumentSet().forEach(financialInfoDocument -> {
                LegalDocuments legalDocuments = financialInfoDocument.getLegalDocuments();
                legalDocumentsRpList.add(convertToLegalDocumentRp(legalDocuments));
            });
            financialInfoRp.setLegalDocumentRpList(legalDocumentsRpList);
            financialInfoRp.setRequestStatus(financialInfo.getRequestStatus().toString());
            financialInfoRp.setExpiredDate(DateUtil.format(DateUtil.DD_MM_YYY_HH_MM_SLASH, financialInfo.getExpiredDate()));
            financialInfoRp.setNote(financialInfo.getNote());
        }
        financialInfoRp.setIsExpired(financialInfo.getIsExpired());
        return financialInfoRp;
    }

    private LegalDocumentRp convertToLegalDocumentRp(LegalDocuments legalDocuments) {
        LegalDocumentRp legalDocumentRp = new LegalDocumentRp();
        legalDocumentRp.setLegalDocumentId(legalDocuments.getId());
        legalDocumentRp.setDescription(legalDocuments.getDescription());
        legalDocumentRp.setImageBase64(legalDocuments.getUrlDocument());
        legalDocumentRp.setDocumentType(legalDocuments.getDocumentType().toString());
        if (legalDocuments.getDocumentGroupId() != null)
            legalDocumentRp.setDocumentGroupId(legalDocuments.getDocumentGroupId().getId());
        legalDocumentRp.setRequestStatus(legalDocuments.getRequestStatus().toString());
        legalDocumentRp.setExpirationDate(DateUtil.format(DateUtil.DD_MM_YYY_HH_MM_SLASH, legalDocuments.getExpirationDate()));
        return legalDocumentRp;
    }
}
