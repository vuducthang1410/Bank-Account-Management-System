package org.demo.loanservice.services.impl;

import com.system.common_library.dto.response.account.AccountInfoDTO;
import com.system.common_library.dto.user.CustomerDetailDTO;
import com.system.common_library.enums.ObjectStatus;
import com.system.common_library.service.AccountDubboService;
import com.system.common_library.service.CustomerDubboService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.*;
import org.demo.loanservice.controllers.exception.DataNotFoundException;
import org.demo.loanservice.controllers.exception.DataNotValidException;
import org.demo.loanservice.controllers.exception.ServerErrorException;
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
import org.demo.loanservice.repositories.LegalDocumentsRepository;
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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FinancialInfoServiceImpl implements IFinancialInfoService {
    private final FinancialInfoRepository financialInfoRepository;
    private final LegalDocumentsRepository legalDocumentsRepository;
    @DubboReference
    private CustomerDubboService customerDubboService;
    @DubboReference
    private AccountDubboService accountDubboService;
    private final Logger log = LogManager.getLogger(FinancialInfoServiceImpl.class);
    private final CICService cicService;
    private final Util util;

    @Override
    @Transactional
    public DataResponseWrapper<Object> saveInfoIndividualCustomer(FinancialInfoRq financialInfoRq, List<MultipartFile> incomeVerificationDocuments, String transactionId) {
        String cifCode = "00000"; // Default CIF code
        CustomerDetailDTO customerInfo;

        // Fetch customer information by CIF code
        try {
            //Validate customer information
            customerInfo = customerDubboService.getCustomerByCifCode(cifCode);
            if (customerInfo == null) {
                log.info(MessageData.MESSAGE_LOG, transactionId, MessageData.CUSTOMER_ACCOUNT_NOT_FOUND.getMessageLog());
                throw new DataNotFoundException(MessageData.CUSTOMER_ACCOUNT_NOT_FOUND.getKeyMessage(), MessageData.CUSTOMER_ACCOUNT_NOT_FOUND.getCode());
            }
            log.debug("Customer status: {}", customerInfo.getStatus());
            if (!customerInfo.getStatus().equals(ObjectStatus.ACTIVE)) {
                log.info(MessageData.MESSAGE_LOG, MessageData.CUSTOMER_ACCOUNT_NOT_ACTIVE.getMessageLog(), transactionId);
                throw new DataNotValidException(MessageData.CUSTOMER_ACCOUNT_NOT_ACTIVE.getKeyMessage(), MessageData.CUSTOMER_ACCOUNT_NOT_ACTIVE.getCode());
            }
        } catch (Exception e) {
            log.info(MessageData.MESSAGE_LOG, transactionId, e.getMessage());
            throw new ServerErrorException(); // Handle server errors gracefully
        }


//         Check if the banking account is active
        AccountInfoDTO bankingAccountInfoDTO = accountDubboService.getBankingAccount(cifCode);
        if (bankingAccountInfoDTO == null) {
            return null;
        }
        if (!bankingAccountInfoDTO.getStatusAccount().equals(ObjectStatus.ACTIVE)) {
            log.info(MessageData.MESSAGE_LOG, MessageData.BANKING_ACCOUNT_NOT_ACTIVE.getMessageLog(), transactionId);
            throw new DataNotValidException(MessageData.BANKING_ACCOUNT_NOT_ACTIVE.getKeyMessage(), MessageData.BANKING_ACCOUNT_NOT_ACTIVE.getCode());
        }

        // Fetch credit score from CIC service (To-Do: Update input parameters dynamically)
        CICResponse cicResponse = cicService.getCreditScore("079123456789", "vu duc thang", "", "0123456789");

        // Create and save financial information record
        FinancialInfo financialInfo = new FinancialInfo();
        financialInfo.setCustomerId(customerInfo.getCustomerId());
        financialInfo.setCustomerNumber(customerInfo.getCustomerNumber());
        financialInfo.setIncome(financialInfoRq.getIncome().stripTrailingZeros().toPlainString());
        financialInfo.setUnit(Unit.valueOf(financialInfoRq.getUnit()));
        financialInfo.setIncomeSource(financialInfoRq.getIncomeSource());
        financialInfo.setIncomeType(financialInfoRq.getIncomeType());
        financialInfo.setRequestStatus(RequestStatus.PENDING);
        financialInfo.setCreditScore(cicResponse.getCreditScore());
        financialInfo.setDebtStatus(cicResponse.getDebtStatus());
        financialInfo.setIsExpired(false);
        financialInfo.setExpiredDate(new Date(DateUtil.getDateOfAfterNMonth(6).getTime())); // Set expiry date to 6 months later
        financialInfo.setIsDeleted(false);
        financialInfo.setLastUpdatedCreditReview(DateUtil.convertStringToTimeStamp(cicResponse.getLastUpdated()));
        financialInfo.setApplicableObjects(ApplicableObjects.INDIVIDUAL_CUSTOMER);
        financialInfo.setLoanAmountMax(BigDecimal.ZERO);
        financialInfoRepository.save(financialInfo);

        // Process income verification documents
        List<LegalDocuments> legalDocumentsList = new ArrayList<>();
        incomeVerificationDocuments.forEach(multipartFile -> {
            LegalDocuments legalDocument = new LegalDocuments();
            legalDocument.setCifCode("123456789"); // ToDo: Retrieve CIF code dynamically from account service
            legalDocument.setDescription("Financial information document");
            legalDocument.setIsDeleted(false);
            legalDocument.setExpirationDate(new Date(DateUtil.getDateOfAfterNMonth(3).getTime())); // Expiry date: 3 months later
            legalDocument.setDocumentType(DocumentType.LOAN_DOCUMENT);
            legalDocument.setUrlDocument(multipartFile.getOriginalFilename()); // ToDo: Upload file to S3 and get URL
            legalDocument.setRequestStatus(RequestStatus.APPROVED);
            legalDocumentsList.add(legalDocument);
        });
        legalDocumentsRepository.saveAll(legalDocumentsList);

        // Associate legal documents with financial information
        legalDocumentsList.forEach(legalDocument -> legalDocumentsRepository.insertFinancialInfoDocument(financialInfo.getId(), legalDocument.getId()));

        // Return response
        return DataResponseWrapper.builder()
                .data(financialInfo.getId())
                .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                .message("Your financial information has been successfully registered and is pending review!") // To-Do: Handle response message dynamically
                .build();
    }


    @Override
    public DataResponseWrapper<Object> getAllInfoIsByStatus(Integer pageNumber, Integer pageSize,
                                                            String status, String transactionId) {

        Pageable page = PageRequest.of(pageNumber, pageSize);
        Page<FinancialInfo> financialInfoPage = financialInfoRepository.findAllByIsDeletedAndRequestStatus(false, RequestStatus.valueOf(status), page);

        List<FinancialInfoRp> financialInfoRpList = financialInfoPage.getContent()
                .stream()
                .map(financialInfo -> convertToFinancialInfoRp(financialInfo, false))
                .toList();

        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("financialInfoRpList", financialInfoRpList);
        dataResponse.put("totalRecords", financialInfoPage.getTotalElements());
        return DataResponseWrapper.builder()
                .data(dataResponse)
                .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                .message("")//todo: handler response message
                .build();
    }

    @Override
    @Cacheable(value = "financial-info", key = "#id", unless = "#result == null")
    public DataResponseWrapper<Object> getDetailInfoById(String id, String transactionId) {
        FinancialInfo financialInfo = financialInfoRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(
                        () -> {
                            log.info(MessageData.MESSAGE_LOG_NOT_FOUND_DATA, transactionId, MessageData.FINANCIAL_INFO_NOT_FOUND.getMessageLog(), id);
                            return new DataNotFoundException(MessageData.FINANCIAL_INFO_NOT_FOUND.getKeyMessage(), MessageData.FINANCIAL_INFO_NOT_FOUND.getCode());//todo
                        }
                );
        FinancialInfoRp financialInfoRp = convertToFinancialInfoRp(financialInfo, true);
        return DataResponseWrapper.builder()
                .data(financialInfoRp)
                .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                .message("")//todo: handler response message
                .build();
    }

    @Override
    @CacheEvict(value = "financial-info", key = "#financialInfoRq.financialInfoId")
    public DataResponseWrapper<Object> approveFinancialInfo(ApproveFinancialInfoRq financialInfoRq, String
            transactionId) {
        FinancialInfo financialInfo = financialInfoRepository.findByIdAndIsDeleted(financialInfoRq.getFinancialInfoId(), false)
                .orElseThrow(
                        () -> {
                            log.info(MessageData.MESSAGE_LOG_NOT_FOUND_DATA, transactionId, MessageData.FINANCIAL_INFO_NOT_FOUND.getMessageLog(), financialInfoRq.getFinancialInfoId());
                            return new DataNotFoundException(MessageData.FINANCIAL_INFO_NOT_FOUND.getKeyMessage(), MessageData.FINANCIAL_INFO_NOT_FOUND.getCode());//todo
                        }
                );
        financialInfo.setRequestStatus(RequestStatus.valueOf(financialInfoRq.getStatusFinancialInfo()));
        financialInfo.setNote(financialInfoRq.getNote());
        financialInfo.setLoanAmountMax(financialInfoRq.getLoanAmountLimit());
        financialInfoRepository.save(financialInfo);
        //todo: call notification service

        return DataResponseWrapper.builder()
                .data(financialInfo.getId())
                .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                .message("")
                .build();
    }

    @Override
    public DataResponseWrapper<Object> verifyFinancialInfo(String transactionId) {
        //todo: get customer id form Security Context
        String customerId = "123456789";
        Optional<FinancialInfo> financialInfoOptional = financialInfoRepository.findByIsDeletedAndCustomerId(false, customerId);
        if (financialInfoOptional.isEmpty()) {
            log.info(MessageData.MESSAGE_LOG, transactionId, "Not found financial info of customer to verify");
            throw new DataNotFoundException(MessageData.FINANCIAL_INFO_NOT_FOUND.getKeyMessage(), MessageData.FINANCIAL_INFO_NOT_FOUND.getCode());
        }
        Boolean isApprove = financialInfoOptional.get().getRequestStatus().equals(RequestStatus.APPROVED);
        return DataResponseWrapper.builder()
                .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                .message("")
                .data(isApprove)
                .build();
    }

    private FinancialInfoRp convertToFinancialInfoRp(FinancialInfo financialInfo, Boolean isDetail) {
        FinancialInfoRp financialInfoRp = new FinancialInfoRp();
        financialInfoRp.setCustomerId(financialInfo.getCustomerId());
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
        financialInfoRp.setAmountLoanLimit(financialInfo.getLoanAmountMax());
        return financialInfoRp;
    }

    @Override
    public FinancialInfo getFinancialInfoByCustomerId(String customerId, String transactionId) {
        return financialInfoRepository.findByIsDeletedAndCustomerId(false, customerId)
                .orElseThrow(() -> {
                    log.info(MessageData.MESSAGE_LOG_NOT_FOUND_DATA, transactionId, "Not found financial info with customer ", customerId);
                    return new DataNotFoundException(MessageData.FINANCIAL_INFO_NOT_FOUND.getKeyMessage(), MessageData.FINANCIAL_INFO_NOT_FOUND.getCode());
                });
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
