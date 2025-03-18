package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.controllers.exception.DataNotValidException;
import org.demo.loanservice.controllers.exception.ServerErrorException;
import org.demo.loanservice.dto.enumDto.FormDeftRepaymentEnum;
import org.demo.loanservice.dto.enumDto.LoanStatus;
import org.demo.loanservice.dto.enumDto.RequestStatus;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.dto.projection.LoanAmountInfoProjection;
import org.demo.loanservice.dto.request.IndividualCustomerInfoRq;
import org.demo.loanservice.dto.request.LoanInfoApprovalRq;
import org.demo.loanservice.entities.FinancialInfo;
import org.demo.loanservice.entities.InterestRate;
import org.demo.loanservice.entities.LoanDetailInfo;
import org.demo.loanservice.entities.LoanProduct;
import org.demo.loanservice.entities.LoanVerificationDocument;
import org.demo.loanservice.repositories.LoanDetailInfoRepository;
import org.demo.loanservice.repositories.LoanVerificationDocumentRepository;
import org.demo.loanservice.services.IFinancialInfoService;
import org.demo.loanservice.services.IInterestRateService;
import org.demo.loanservice.services.ILoanDetailInfoService;
import org.demo.loanservice.services.ILoanProductService;
import org.demo.loanservice.services.IPaymentScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanDetailInfoServiceImpl implements ILoanDetailInfoService {
    private final LoanDetailInfoRepository loanDetailInfoRepository;
    private final LoanVerificationDocumentRepository loanVerificationDocumentRepository;
    private final IInterestRateService interestRateService;
    private final ILoanProductService loanProductService;
    private final IFinancialInfoService financialInfoService;
    private final IPaymentScheduleService paymentScheduleService;
    //    @DubboReference
//    private NotificationDubboService notificationDubboService;
//    @DubboReference
//    private CustomerDubboService customerDubboService;
//    @DubboReference
//    private TransactionDubboService transactionDubboService;
//    @DubboReference
//    private AccountDubboService accountDubboService;
    private final Logger log = LogManager.getLogger(LoanDetailInfoServiceImpl.class);

    @Override
    public DataResponseWrapper<Object> registerIndividualCustomerLoan(IndividualCustomerInfoRq individualCustomerInfoRq, String transactionId) {
        // Retrieve the CIF code of the authenticated customer
//        String customerId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        String customerId = "123456789";
        // Retrieve financial information for the customer
        FinancialInfo financialInfo = financialInfoService.getFinancialInfoByCustomerId(customerId, transactionId);

        // Validate if the financial information is approved
        if (!financialInfo.getRequestStatus().equals(RequestStatus.APPROVED)) {
            log.info(MessageData.MESSAGE_LOG, transactionId, MessageData.FINANCIAL_INFO_NOT_APPROVE.getMessageLog(), financialInfo.getRequestStatus());
            throw new DataNotValidException(MessageData.FINANCIAL_INFO_NOT_APPROVE.getKeyMessage(),
                    MessageData.FINANCIAL_INFO_NOT_APPROVE.getCode());
        }

        // Retrieve the loan product details
        LoanProduct loanProduct = loanProductService.getLoanProductById(individualCustomerInfoRq.getLoanProductId(), transactionId);

        // Validate if the requested loan amount does not exceed the product's loan limit
        if (loanProduct.getLoanLimit().compareTo(individualCustomerInfoRq.getLoanAmount()) < 0) {
            log.info(MessageData.MESSAGE_LOG, transactionId, MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getMessageLog(), loanProduct.getLoanLimit().toPlainString());
            throw new DataNotValidException(MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getKeyMessage(),
                    MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getCode());
        }

        // Validate if the requested loan term does not exceed the product's term limit
        if (loanProduct.getTermLimit().compareTo(individualCustomerInfoRq.getLoanTerm()) < 0) {
            log.info(MessageData.MESSAGE_LOG, transactionId, MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getMessageLog(), loanProduct.getTermLimit());
            throw new DataNotValidException(MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getKeyMessage(),
                    MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getCode());
        }

        Optional<LoanAmountInfoProjection> loanAmountInfoProjectionOptional = loanDetailInfoRepository.getMaxLoanLimitAndCurrentLoanAmount(customerId);
        if (loanAmountInfoProjectionOptional.isEmpty()) {
            log.info("Not found loan amount info for loan detail info");
            throw new ServerErrorException();
        }
        LoanAmountInfoProjection loanAmountInfoProjection = loanAmountInfoProjectionOptional.get();
        BigDecimal expectedLoanAmount = individualCustomerInfoRq.getLoanAmount().add(loanAmountInfoProjection.getTotalLoanedAmount());
        if (expectedLoanAmount.compareTo(loanAmountInfoProjection.getLoanAmountMax()) > 0) {
            log.info("transactionId: {} - expected loan amount: {} - loan amount limit of customer: {}",
                    transactionId,
                    expectedLoanAmount.toPlainString(),
                    loanAmountInfoProjection.getLoanAmountMax().toPlainString());
            throw new DataNotValidException(MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getKeyMessage(),
                    MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getCode());
        }

        // Retrieve the applicable interest rate based on the loan amount
        InterestRate interestRate = interestRateService.getInterestRateByLoanAmount(individualCustomerInfoRq.getLoanAmount(), transactionId);
        log.debug("transactionId:{} - Loan amount : {} - loan term {} ",
                transactionId,
                individualCustomerInfoRq.getLoanAmount(),
                individualCustomerInfoRq.getLoanTerm());
        log.debug("transactionId:{} - interest rate: {} - minimum amount: {} - minimum term: {}",
                transactionId, interestRate.getInterestRate(),
                interestRate.getMinimumAmount(),
                interestRate.getMinimumLoanTerm());


        String loanAccountId = "123456789";
//        try {
//            loanAccountId = accountDubboService.createLoanAccount(customerId);
//        } catch (Exception e) {
//            log.error(MessageData.MESSAGE_LOG, transactionId, e.getMessage());
//            throw new DataNotValidException(MessageData.CREATED_LOAN_ACCOUNT_ERROR.getKeyMessage(),
//                    MessageData.CREATED_LOAN_ACCOUNT_ERROR.getCode());
//        }

        // Check for null separately
        if (loanAccountId == null) {
            log.error(MessageData.MESSAGE_LOG, transactionId, "Loan account creation failed.");
            throw new DataNotValidException(MessageData.CREATED_LOAN_ACCOUNT_ERROR.getKeyMessage(),
                    MessageData.CREATED_LOAN_ACCOUNT_ERROR.getCode());
        }
        // Create a new loan detail entry
        LoanDetailInfo loanDetailInfo = new LoanDetailInfo();
        loanDetailInfo.setLoanAmount(individualCustomerInfoRq.getLoanAmount());
        loanDetailInfo.setLoanTerm(individualCustomerInfoRq.getLoanTerm());
        loanDetailInfo.setRequestStatus(RequestStatus.PENDING);
        loanDetailInfo.setLoanProductId(loanProduct);
        loanDetailInfo.setFormDeftRepayment(FormDeftRepaymentEnum.valueOf(individualCustomerInfoRq.getFormDeftRepayment()));
        loanDetailInfo.setLoanStatus(LoanStatus.PENDING);
        loanDetailInfo.setUnit(Unit.valueOf(individualCustomerInfoRq.getLoanUnit()));
        loanDetailInfo.setInterestRate(interestRate.getInterestRate());
        loanDetailInfo.setCreditScore(financialInfo.getCreditScore());
        loanDetailInfo.setIncome(financialInfo.getIncome());
        loanDetailInfo.setIncomeSource(financialInfo.getIncomeSource());
        loanDetailInfo.setIncomeType(financialInfo.getIncomeType());
        loanDetailInfo.setDebtStatus(financialInfo.getDebtStatus());
        loanDetailInfo.setIsDeleted(false);
        loanDetailInfo.setLastUpdatedCreditReview(financialInfo.getLastUpdatedCreditReview());
        loanDetailInfo.setLoanAccountId(loanAccountId);
        loanDetailInfo.setFinancialInfo(financialInfo);
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
    @Transactional
    public DataResponseWrapper<Object> approveIndividualCustomerDisbursement(LoanInfoApprovalRq loanInfoApprovalRq, String transactionId) {
        LoanDetailInfo loanDetailInfo = loanDetailInfoRepository
                .findByIdAndIsDeleted(loanInfoApprovalRq.getLoanDetailInfoId(), false)
                .orElseThrow(() -> {
                    log.info(MessageData.MESSAGE_LOG, transactionId, MessageData.LOAN_DETAIL_INFO_NOT_FOUND, loanInfoApprovalRq.getLoanDetailInfoId());
                    return new DataNotValidException(MessageData.LOAN_DETAIL_INFO_NOT_FOUND.getKeyMessage(),
                            MessageData.LOAN_DETAIL_INFO_NOT_FOUND.getCode());
                });

        log.debug("Request status of loan info approved:{}", loanInfoApprovalRq.getRequestStatus());
        if (loanInfoApprovalRq.getRequestStatus().equalsIgnoreCase(RequestStatus.REJECTED.name())) {
            loanDetailInfo.setRequestStatus(RequestStatus.valueOf(loanInfoApprovalRq.getRequestStatus()));
            loanDetailInfo.setNote(loanDetailInfo.getNote());
            loanDetailInfo.setLoanStatus(LoanStatus.REJECTED);
            loanDetailInfoRepository.save(loanDetailInfo);
            return DataResponseWrapper.builder()
                    .data(loanDetailInfo.getId())
                    .message("")
                    .status("00000")
                    .build();
        }

        //todo: verify info account of customer
//        AccountInfoRp accountInfo;
//        try {
//            accountInfo = accountDubboService.getAccountInfoByLoanAccountId(loanDetailInfo.getLoanAccountId());
//        } catch (Exception e) {
//            log.info(MessageData.MESSAGE_LOG, transactionId, e.getMessage());
//            throw new ServerErrorException();
//        }
//        if (accountInfo == null) {
//            return null;
//        }
//        if (!accountInfo.getStatusLoanAccount().equalsIgnoreCase("ACTIVE")) {
//            return null;
//        }
//        if (!accountInfo.getStatusBankingAccount().equalsIgnoreCase("ACTIVE")) {
//            return null;
//        }
//        if (!StringUtils.hasText(accountInfo.getBankingAccountNumber()) || !StringUtils.hasText(accountInfo.getLoanAccountNumber())) {
//            return null;
//        }

        //todo: execute Disbursement
        try {
            //todo: Disbursement for loan
//            var transactionResponse = transactionDubboService.transfer();


            //todo: handler response from transaction service


            loanDetailInfo.setRequestStatus(RequestStatus.valueOf(loanInfoApprovalRq.getRequestStatus()));
            loanDetailInfo.setNote(loanDetailInfo.getNote());
            loanDetailInfo.setLoanStatus(LoanStatus.ACTIVE);
            loanDetailInfo.setLoanDate(DateUtil.getDateAfterNDay(1));
            loanDetailInfoRepository.save(loanDetailInfo);

            //todo: generate deft repayment schedule
            paymentScheduleService.createDeftRepaymentInfo(loanDetailInfo);
        } catch (Exception e) {
            log.info("");
            return null;
        }
        return DataResponseWrapper.builder()
                .data(loanDetailInfo.getId())
                .message("")
                .status("00000")
                .build();
    }
}
