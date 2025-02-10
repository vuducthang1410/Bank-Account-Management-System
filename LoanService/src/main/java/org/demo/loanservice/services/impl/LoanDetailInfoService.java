package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.controllers.exception.DataNotValidException;
import org.demo.loanservice.dto.enumDto.LoanStatus;
import org.demo.loanservice.dto.enumDto.RequestStatus;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.dto.request.IndividualCustomerInfoRq;
import org.demo.loanservice.entities.FinancialInfo;
import org.demo.loanservice.entities.FormDeftRepayment;
import org.demo.loanservice.entities.InterestRate;
import org.demo.loanservice.entities.LoanDetailInfo;
import org.demo.loanservice.entities.LoanProduct;
import org.demo.loanservice.entities.LoanVerificationDocument;
import org.demo.loanservice.repositories.LoanDetailInfoRepository;
import org.demo.loanservice.repositories.LoanVerificationDocumentRepository;
import org.demo.loanservice.services.IFinancialInfoService;
import org.demo.loanservice.services.IFormDeftRepaymentService;
import org.demo.loanservice.services.IInterestRateService;
import org.demo.loanservice.services.ILoanDetailInfoService;
import org.demo.loanservice.services.ILoanProductService;
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
    private final LoanVerificationDocumentRepository loanVerificationDocumentRepository;
    private final IInterestRateService interestRateService;
    private final IFormDeftRepaymentService formDeftRepaymentService;
    private final ILoanProductService loanProductService;
    private final IFinancialInfoService financialInfoService;
    private final Logger log = LogManager.getLogger(LoanDetailInfoService.class);

    @Override
    public DataResponseWrapper<Object> registerIndividualCustomerLoan(IndividualCustomerInfoRq individualCustomerInfoRq, String transactionId) {
        // Retrieve the CIF code of the authenticated customer
        String cifCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        // Retrieve financial information for the customer
        FinancialInfo financialInfo = financialInfoService.getFinancialInfoByCifCode(cifCode, transactionId);

        // Validate if the financial information is approved
        if (!financialInfo.getRequestStatus().equals(RequestStatus.APPROVED)) {
            log.info(MessageData.MESSAGE_LOG, MessageData.FINANCIAL_INFO_NOT_APPROVE.getMessageLog(), transactionId);
            throw new DataNotValidException(MessageData.FINANCIAL_INFO_NOT_APPROVE.getKeyMessage(),
                    MessageData.FINANCIAL_INFO_NOT_APPROVE.getCode());
        }

        // Retrieve the loan product details
        LoanProduct loanProduct = loanProductService.getLoanProductById(individualCustomerInfoRq.getLoanProductId(), transactionId);

        // Validate if the requested loan amount does not exceed the product's loan limit
        if (loanProduct.getLoanLimit().compareTo(individualCustomerInfoRq.getLoanAmount()) < 0) {
            log.info(MessageData.MESSAGE_LOG, MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getMessageLog(), loanProduct.getLoanLimit().toPlainString(), transactionId);
            throw new DataNotValidException(MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getKeyMessage(),
                    MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getCode());
        }

        // Validate if the requested loan term does not exceed the product's term limit
        if (loanProduct.getTermLimit().compareTo(individualCustomerInfoRq.getLoanTerm()) < 0) {
            log.info(MessageData.MESSAGE_LOG, MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getMessageLog(), loanProduct.getTermLimit(), transactionId);
            throw new DataNotValidException(MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getKeyMessage(),
                    MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getCode());
        }

        // Retrieve the repayment method details
        FormDeftRepayment formDeftRepayment = formDeftRepaymentService.getFormDeftRepaymentById(individualCustomerInfoRq.getFormDeftRepaymentId(), transactionId);

        // Retrieve the applicable interest rate based on the loan amount
        InterestRate interestRate = interestRateService.getInterestRateByLoanAmount(individualCustomerInfoRq.getLoanAmount(), transactionId);

        // Create a new loan detail entry
        LoanDetailInfo loanDetailInfo = new LoanDetailInfo();
        loanDetailInfo.setCifCode(cifCode);
        loanDetailInfo.setLoanAmount(individualCustomerInfoRq.getLoanAmount());
        loanDetailInfo.setLoanTerm(individualCustomerInfoRq.getLoanTerm());
        loanDetailInfo.setRequestStatus(RequestStatus.PENDING);
        loanDetailInfo.setLoanProductId(loanProduct);
        loanDetailInfo.setFormDeftRepaymentId(formDeftRepayment);
        loanDetailInfo.setLoanStatus(LoanStatus.PENDING_APPROVAL);
        loanDetailInfo.setUnit(Unit.valueOf(individualCustomerInfoRq.getLoanUnit()));
        loanDetailInfo.setInterestRate(interestRate.getInterestRate());
        loanDetailInfo.setCreditScore(financialInfo.getCreditScore());
        loanDetailInfo.setIncome(financialInfo.getIncome());
        loanDetailInfo.setIncomeSource(financialInfo.getIncomeSource());
        loanDetailInfo.setIncomeType(financialInfo.getIncomeType());
        loanDetailInfo.setDebtStatus(financialInfo.getDebtStatus());
        loanDetailInfo.setLastUpdatedCreditReview(financialInfo.getLastUpdatedCreditReview());

        // Save loan details to the database
        loanDetailInfoRepository.save(loanDetailInfo);

        // Create and store loan verification documents based on the customer's financial information documents
        List<LoanVerificationDocument> loanVerificationDocumentList = new ArrayList<>();
        financialInfo.getFinancialInfoDocumentSet().forEach(financialInfoDocument -> {
            LoanVerificationDocument loanVerificationDocument = new LoanVerificationDocument();
            loanVerificationDocument.setLoanDetailInfo(loanDetailInfo);
            loanVerificationDocument.setLegalDocuments(financialInfoDocument.getLegalDocuments());
            loanVerificationDocumentList.add(loanVerificationDocument);
        });

        // Save all loan verification documents
        loanVerificationDocumentRepository.saveAll(loanVerificationDocumentList);

        return DataResponseWrapper.builder()
                .data(loanDetailInfo.getId())
                .message("Loan registration successful")
                .status("00000")
                .build();
    }

    @Override
    public DataResponseWrapper<Object> approveIndividualCustomerDisbursement(String id, String transactionId) {
        Optional<LoanDetailInfo> loanDetailInfo = loanDetailInfoRepository.findByIdAndIsDeleted(id, false);
        if (loanDetailInfo.isEmpty()) {
            log.info(MessageData.MESSAGE_LOG, MessageData.LOAN_DETAIL_INFO_NOT_FOUND, id, transactionId);
            throw new DataNotValidException(MessageData.LOAN_DETAIL_INFO_NOT_FOUND.getKeyMessage(),
                    MessageData.LOAN_DETAIL_INFO_NOT_FOUND.getCode());
        }

        return null;
    }

}
