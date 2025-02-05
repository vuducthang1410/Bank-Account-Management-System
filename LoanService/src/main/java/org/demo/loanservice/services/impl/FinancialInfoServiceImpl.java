package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.dto.CICResponse;
import org.demo.loanservice.dto.enumDto.ApplicableObjects;
import org.demo.loanservice.dto.enumDto.DocumentType;
import org.demo.loanservice.dto.enumDto.RequestStatus;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.dto.request.FinancialInfoRq;
import org.demo.loanservice.entities.FinancialInfo;
import org.demo.loanservice.entities.LegalDocuments;
import org.demo.loanservice.repositories.CustomerLoanInfoRepository;
import org.demo.loanservice.repositories.FinancialInfoRepository;
import org.demo.loanservice.repositories.InterestRateRepository;
import org.demo.loanservice.repositories.LegalDocumentsRepository;
import org.demo.loanservice.services.IFinancialInfoService;
import org.demo.loanservice.wiremockService.CICService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FinancialInfoServiceImpl implements IFinancialInfoService {
    private final InterestRateRepository interestRateRepository;
    private final FinancialInfoRepository financialInfoRepository;
    private final CustomerLoanInfoRepository customerLoanInfoRepository;
    private final LegalDocumentsRepository legalDocumentsRepository;
    private final CICService cicService;

    @Override
    @Transactional
    public DataResponseWrapper<Object> saveInfoIndividualCustomer(
            FinancialInfoRq financialInfoRq,
            List<MultipartFile> incomeVerificationDocuments,
            String transactionId) {

        CICResponse cicResponse=cicService.getCreditScore("079123456789","vu duc thang","","0123456789");
        FinancialInfo financialInfo = new FinancialInfo();
        financialInfo.setCustomerId(transactionId);//todo
        financialInfo.setIncome(financialInfoRq.getIncome());
        financialInfo.setUnit(Unit.valueOf(financialInfoRq.getUnit()));
        financialInfo.setIncomeSource(financialInfoRq.getIncomeSource());
        financialInfo.setIncomeType(financialInfoRq.getIncomeType());
        financialInfo.setIsApproved(false);
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
            legalDocument.setCustomerId(transactionId);//todo
            legalDocument.setDescription("Tài liệu về thông tin tài chính");
            legalDocument.setIsDeleted(false);
            legalDocument.setExpirationDate(new Date(DateUtil.getDateOfAfterNMonth(3).getTime()));
            legalDocument.setDocumentType(DocumentType.LOAN_DOCUMENT);
            legalDocument.setUrlDocument(multipartFile.getOriginalFilename());//todo
            legalDocument.setRequestStatus(RequestStatus.PENDING);
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
                .message("Thông tin tài chính của bạn được đăng ký thành công và đang chờ xét duyệt!!!")
                .build();
    }

    @Override
    public DataResponseWrapper<Object> getAllInfoIsPending(
            Integer pageNumber,
            Integer pageSize,
            String transactionId) {
        Pageable page= PageRequest.of(pageNumber, pageSize);

        return null;
    }

}
