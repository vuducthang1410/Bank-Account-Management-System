package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.controllers.exception.DataNotFoundException;
import org.demo.loanservice.controllers.exception.DataNotValidException;
import org.demo.loanservice.dto.enumDto.LoanStatus;
import org.demo.loanservice.dto.enumDto.RequestStatus;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.dto.request.IndividualCustomerInfoRq;
import org.demo.loanservice.entities.FinancialInfo;
import org.demo.loanservice.entities.FormDeftRepayment;
import org.demo.loanservice.entities.LoanDetailInfo;
import org.demo.loanservice.entities.LoanProduct;
import org.demo.loanservice.entities.LoanVerificationDocument;
import org.demo.loanservice.repositories.FinancialInfoRepository;
import org.demo.loanservice.repositories.FormDeftRepaymentRepository;
import org.demo.loanservice.repositories.LoanDetailInfoRepository;
import org.demo.loanservice.repositories.LoanProductRepository;
import org.demo.loanservice.repositories.LoanVerificationDocumentRepository;
import org.demo.loanservice.services.ILoanDetailInfoService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanDetailInfoService implements ILoanDetailInfoService {
    private final LoanDetailInfoRepository loanDetailInfoRepository;
    private final FinancialInfoRepository financialInfoRepository;
    private final FormDeftRepaymentRepository formDeftRepaymentRepository;
    private final LoanProductRepository loanProductRepository;
    private final LoanVerificationDocumentRepository loanVerificationDocumentRepository;

    private final Logger log = LogManager.getLogger(LoanDetailInfoService.class);

    @Override
    public DataResponseWrapper<Object> registerIndividualCustomerLoan(IndividualCustomerInfoRq individualCustomerInfoRq, String transactionId) {
        String cifCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        Optional<FinancialInfo> financialInfoOptional = financialInfoRepository.findByIsDeletedAndCifCode(false, cifCode);
        if (financialInfoOptional.isEmpty()) {
            log.info(MessageData.MESSAGE_LOG, MessageData.FINANCIAL_INFO_NOT_FOUND.getMessageLog(), transactionId);
            throw new DataNotFoundException(MessageData.FINANCIAL_INFO_NOT_FOUND.getKeyMessage(), MessageData.FINANCIAL_INFO_NOT_FOUND.getCode());//todo
        }

        FinancialInfo financialInfo = financialInfoOptional.get();
        if (!financialInfo.getRequestStatus().equals(RequestStatus.APPROVED)) {
            log.info(MessageData.MESSAGE_LOG, MessageData.FINANCIAL_INFO_NOT_APPROVE.getMessageLog(), transactionId);
            throw new DataNotValidException(MessageData.FINANCIAL_INFO_NOT_APPROVE.getKeyMessage(), MessageData.FINANCIAL_INFO_NOT_APPROVE.getCode()); //todo
        }

        Optional<LoanProduct> loanProductOptional = loanProductRepository.findByIdAndIsDeleted(individualCustomerInfoRq.getLoanProductId(), false);
        if (loanProductOptional.isEmpty()) {
            log.info(MessageData.MESSAGE_LOG, MessageData.LOAN_PRODUCT_NOT_FOUNT.getMessageLog(), transactionId);
            throw new DataNotValidException(MessageData.LOAN_PRODUCT_NOT_FOUNT.getKeyMessage(), MessageData.LOAN_PRODUCT_NOT_FOUNT.getCode());
        }
        LoanProduct loanProduct = loanProductOptional.get();
        if (loanProduct.getLoanLimit().compareTo(individualCustomerInfoRq.getLoanAmount()) < 0) {
            log.info(MessageData.MESSAGE_LOG, MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getMessageLog(), transactionId);
            throw new DataNotValidException(MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getKeyMessage(), MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getCode());//todo handler
        }
        if (loanProduct.getTermLimit().compareTo(individualCustomerInfoRq.getLoanTerm()) < 0) {
            throw new DataNotValidException(MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getKeyMessage(), MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getCode());//todo handler
        }

        Optional<FormDeftRepayment> formDeftRepaymentOptional = formDeftRepaymentRepository.findByIdAndIsDeleted(individualCustomerInfoRq.getFormDeftRepaymentId(), false);
        if (formDeftRepaymentOptional.isEmpty()) {
            log.info(MessageData.MESSAGE_LOG, MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getMessageLog(), transactionId);
            throw new DataNotFoundException(MessageData.FORM_DEFT_REPAYMENT_NOT_FOUNT.getKeyMessage(), MessageData.FORM_DEFT_REPAYMENT_NOT_FOUNT.getCode());
        }
        FormDeftRepayment formDeftRepayment = formDeftRepaymentOptional.get();
        LoanDetailInfo loanDetailInfo = new LoanDetailInfo();
        loanDetailInfo.setCifCode(cifCode);
        loanDetailInfo.setLoanAmount(individualCustomerInfoRq.getLoanAmount());
        loanDetailInfo.setLoanTerm(individualCustomerInfoRq.getLoanTerm());
        loanDetailInfo.setRequestStatus(RequestStatus.PENDING);
        loanDetailInfo.setLoanProductId(loanProduct);
        loanDetailInfo.setFormDeftRepaymentId(formDeftRepayment);
        loanDetailInfo.setLoanStatus(LoanStatus.PENDING_APPROVAL);
        loanDetailInfo.setUnit(Unit.valueOf(individualCustomerInfoRq.getLoanUnit()));
        loanDetailInfo.setInterestRate(loanProduct.getInterestRate().getInterestRate());

        loanDetailInfo.setCreditScore(financialInfo.getCreditScore());
        loanDetailInfo.setIncome(financialInfo.getIncome());
        loanDetailInfo.setIncomeSource(financialInfo.getIncomeSource());
        loanDetailInfo.setIncomeType(financialInfo.getIncomeType());
        loanDetailInfo.setDebtStatus(financialInfo.getDebtStatus());
        loanDetailInfo.setLastUpdatedCreditReview(financialInfo.getLastUpdatedCreditReview());

        loanDetailInfoRepository.save(loanDetailInfo);
        //todo:
        List<LoanVerificationDocument> loanVerificationDocumentList = new ArrayList<>();
        financialInfo.getFinancialInfoDocumentSet().forEach(financialInfoDocument -> {
            LoanVerificationDocument loanVerificationDocument = new LoanVerificationDocument();
            loanVerificationDocument.setLoanDetailInfo(loanDetailInfo);
            loanVerificationDocument.setLegalDocuments(financialInfoDocument.getLegalDocuments());
            loanVerificationDocumentList.add(loanVerificationDocument);
        });
        loanVerificationDocumentRepository.saveAll(loanVerificationDocumentList);
        return DataResponseWrapper.builder()
                .data(loanDetailInfo.getCifCode())
                .message("")
                .status("00000")
                .build();
    }
}
