package org.demo.loanservice.services.impl;

import com.system.common_library.dto.account.CreateLoanDTO;
import com.system.common_library.dto.response.account.AccountInfoDTO;
import com.system.common_library.dto.response.account.LoanAccountInfoDTO;
import com.system.common_library.dto.transaction.loan.CreateLoanDisbursementTransactionDTO;
import com.system.common_library.dto.transaction.loan.TransactionLoanResultDTO;
import com.system.common_library.dto.user.CustomerDetailDTO;
import com.system.common_library.enums.ObjectStatus;
import com.system.common_library.service.AccountDubboService;
import com.system.common_library.service.CustomerDubboService;
import com.system.common_library.service.TransactionDubboService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.common.MessageValue;
import org.demo.loanservice.controllers.exception.DataNotValidException;
import org.demo.loanservice.controllers.exception.ServerErrorException;
import org.demo.loanservice.dto.enumDto.FormDeftRepaymentEnum;
import org.demo.loanservice.dto.enumDto.LoanStatus;
import org.demo.loanservice.dto.enumDto.RequestStatus;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.dto.projection.LoanAmountInfoProjection;
import org.demo.loanservice.dto.request.IndividualCustomerInfoRq;
import org.demo.loanservice.dto.request.LoanInfoApprovalRq;
import org.demo.loanservice.entities.*;
import org.demo.loanservice.repositories.DisbursementInfoHistoryRepository;
import org.demo.loanservice.repositories.LoanDetailInfoRepository;
import org.demo.loanservice.repositories.LoanVerificationDocumentRepository;
import org.demo.loanservice.services.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final ILoanDetailRepaymentScheduleService loanDetailRepaymentScheduleService;
    private final DisbursementInfoHistoryRepository disbursementInfoHistoryRepository;
    @DubboReference
    private CustomerDubboService customerDubboService;
    @DubboReference
    private TransactionDubboService transactionDubboService;
    @DubboReference
    private AccountDubboService accountDubboService;
    private final Logger log = LogManager.getLogger(LoanDetailInfoServiceImpl.class);

    @Override
    public DataResponseWrapper<Object> registerIndividualCustomerLoan(IndividualCustomerInfoRq individualCustomerInfoRq, String transactionId) {
        // Retrieve the CIF code of the authenticated customer
        String cifCode = "00000"; // Default CIF code
        CustomerDetailDTO customerInfo = customerDubboService.getCustomerByCifCode(cifCode);
        // Retrieve financial information for the customer
        FinancialInfo financialInfo = financialInfoService.getFinancialInfoByCustomerId(customerInfo.getCustomerId(), transactionId);

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
            log.info(MessageData.MESSAGE_LOG, transactionId, MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getMessageLog(), loanProduct.getLoanLimit().toPlainString());
            throw new DataNotValidException(MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getKeyMessage(),
                    MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getCode());
        }

        // Validate if the requested loan term does not exceed the product's term limit
        if (loanProduct.getTermLimit().compareTo(individualCustomerInfoRq.getLoanTerm()) < 0) {
            log.info(MessageData.MESSAGE_LOG, MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getMessageLog(), loanProduct.getTermLimit(), transactionId);
            throw new DataNotValidException(MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getKeyMessage(),
                    MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getCode());
        }

        Optional<LoanAmountInfoProjection> loanAmountInfoProjectionOptional = loanDetailInfoRepository.getMaxLoanLimitAndCurrentLoanAmount(customerInfo.getCustomerId());
        if (loanAmountInfoProjectionOptional.isEmpty()) {
            log.info("du lieu khong hop le");
            return null;
        }
        LoanAmountInfoProjection loanAmountInfoProjection = loanAmountInfoProjectionOptional.get();
        BigDecimal expectedLoanAmount = individualCustomerInfoRq.getLoanAmount().add(loanAmountInfoProjection.getTotalLoanedAmount());
        if (expectedLoanAmount.compareTo(loanAmountInfoProjection.getLoanAmountMax()) > 0) {
            log.info("transactionId: {} - expected loan amount: {} - loan amount limit of customer: {}",
                    transactionId,
                    expectedLoanAmount.toPlainString(),
                    loanAmountInfoProjection.getLoanAmountMax().toPlainString());

            return null;
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

        // Create a new loan detail entry
        LoanDetailInfo loanDetailInfo = new LoanDetailInfo();
        loanDetailInfo.setLoanAmount(individualCustomerInfoRq.getLoanAmount());
        loanDetailInfo.setLoanTerm(individualCustomerInfoRq.getLoanTerm());
        loanDetailInfo.setRequestStatus(RequestStatus.PENDING);
        loanDetailInfo.setLoanProductId(loanProduct);
        loanDetailInfo.setFormDeftRepayment(FormDeftRepaymentEnum.valueOf(individualCustomerInfoRq.getFormDeftRepayment()));
        loanDetailInfo.setLoanStatus(LoanStatus.PENDING);
        loanDetailInfo.setUnit(Unit.valueOf(individualCustomerInfoRq.getLoanUnit()));
        loanDetailInfo.setFinancialInfo(financialInfo);
        loanDetailInfo.setInterestRate(interestRate.getInterestRate());
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
        //todo:Call notification service
        return DataResponseWrapper.builder()
                .data(loanDetailInfo.getId())
                .message("Loan registration successful")
                .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                .build();
    }

    @Override
    @Transactional
    public DataResponseWrapper<Object> approveIndividualCustomerDisbursement(LoanInfoApprovalRq loanInfoApprovalRq, String transactionId) {
        LoanDetailInfo loanDetailInfo = loanDetailRepaymentScheduleService.getLoanDetailInfoById(loanInfoApprovalRq.getLoanDetailInfoId(), transactionId);

        log.debug("Request status of loan info approved:{}", loanInfoApprovalRq.getRequestStatus());
        if (loanInfoApprovalRq.getRequestStatus().equalsIgnoreCase(RequestStatus.REJECTED.name())) {
            loanDetailInfo.setRequestStatus(RequestStatus.valueOf(loanInfoApprovalRq.getRequestStatus()));
            loanDetailInfo.setNote(loanDetailInfo.getNote());
            loanDetailInfo.setLoanStatus(LoanStatus.REJECTED);
            loanDetailInfoRepository.save(loanDetailInfo);
            return DataResponseWrapper.builder()
                    .data(loanDetailInfo.getId())
                    .message("")
                    .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                    .build();
        }

        try {
            // Retrieve customer banking account information
            AccountInfoDTO bankingAccountDTO = accountDubboService.getBankingAccount(loanDetailInfo.getFinancialInfo().getCifCode());


            //create loan account
            CreateLoanDTO createLoanDTO = new CreateLoanDTO();
            LoanAccountInfoDTO loanAccountInfoDTO = accountDubboService.createLoanAccount(loanDetailInfo.getFinancialInfo().getCustomerId(), createLoanDTO);
            // Check for null separately
            if (loanAccountInfoDTO == null) {
                log.error(MessageData.MESSAGE_LOG, transactionId, "Loan account creation failed.");
                throw new DataNotValidException(MessageData.CREATED_LOAN_ACCOUNT_ERROR.getKeyMessage(),
                        MessageData.CREATED_LOAN_ACCOUNT_ERROR.getCode());
            }
            if (bankingAccountDTO == null||!StringUtils.hasText(bankingAccountDTO.getAccountNumber())) {
                log.error("transactionId : {} :: Execute error while get banking account. customer id: {}", transactionId, loanDetailInfo.getFinancialInfo().getCustomerId());
                throw new DataNotValidException(MessageData.BANKING_ACCOUNT_NOT_EXITS.getKeyMessage(),
                        MessageData.BANKING_ACCOUNT_NOT_EXITS.getCode());
            }
            if (!bankingAccountDTO.getStatusAccount().equals(ObjectStatus.ACTIVE) || !loanAccountInfoDTO.getStatusLoanAccount().equals(ObjectStatus.ACTIVE)) {
                log.error("transactionId : {} :: Execute error while get banking account. customer id: {}", transactionId, loanDetailInfo.getFinancialInfo().getCustomerId());
                throw new DataNotValidException(MessageData.BANKING_ACCOUNT_NOT_ACTIVE.getKeyMessage(),
                        MessageData.BANKING_ACCOUNT_NOT_ACTIVE.getCode());
            }

            //execute Disbursement

            //Disbursement for loan
            CreateLoanDisbursementTransactionDTO loanDisbursementTransactionDTO = new CreateLoanDisbursementTransactionDTO();
            TransactionLoanResultDTO transactionResponse = transactionDubboService.createLoanAccountDisbursement(loanDisbursementTransactionDTO);
            if (transactionResponse == null) {
                return null;
            }
            //handler response from transaction service
            loanDetailInfo.setRequestStatus(RequestStatus.valueOf(loanInfoApprovalRq.getRequestStatus()));
            loanDetailInfo.setNote(loanDetailInfo.getNote());
            loanDetailInfo.setLoanStatus(LoanStatus.ACTIVE);
            loanDetailInfo.setLoanDate(DateUtil.getDateAfterNDay(1));

            //create disbursement info history
            DisbursementInfoHistory disbursementInfoHistory = getDisbursementInfoHistory(loanDetailInfo, loanAccountInfoDTO, transactionResponse);
            disbursementInfoHistoryRepository.save(disbursementInfoHistory);
            loanDetailInfoRepository.save(loanDetailInfo);
            //generate deft repayment schedule
            paymentScheduleService.createDeftRepaymentInfo(loanDetailInfo);

            //todo: call notification service

        } catch (Exception e) {
            log.info(MessageData.MESSAGE_LOG, transactionId, e.getMessage());
            throw new ServerErrorException();
        }
        return DataResponseWrapper.builder()
                .data(loanDetailInfo.getId())
                .message("")
                .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                .build();
    }

    private static @NotNull DisbursementInfoHistory getDisbursementInfoHistory(LoanDetailInfo loanDetailInfo,
                                                                               LoanAccountInfoDTO loanAccountInfoDTO,
                                                                               TransactionLoanResultDTO transactionLoanResultDTO) {
        DisbursementInfoHistory disbursementInfoHistory = new DisbursementInfoHistory();
        disbursementInfoHistory.setLoanDetailInfo(loanDetailInfo);
        FinancialInfo financialInfo = loanDetailInfo.getFinancialInfo();
        disbursementInfoHistory.setIncome(financialInfo.getIncome());
        disbursementInfoHistory.setCreditScore(financialInfo.getCreditScore());
        disbursementInfoHistory.setIncomeSource(financialInfo.getIncomeSource());
        disbursementInfoHistory.setIncomeType(financialInfo.getIncomeType());
        disbursementInfoHistory.setDebtStatus(financialInfo.getDebtStatus());
        disbursementInfoHistory.setLastUpdatedCreditReview(financialInfo.getLastUpdatedCreditReview());
        disbursementInfoHistory.setLoanAccountId(loanAccountInfoDTO.getAccountLoanId());
        disbursementInfoHistory.setAmountDisbursement(transactionLoanResultDTO.getBalanceLoanAccount());
        return disbursementInfoHistory;
    }

    @Override
    public DataResponseWrapper<Object> getAllByLoanStatus(String loanStatus, Integer pageNumber, Integer pageSize, String transactionId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate"));
        Page<LoanDetailInfo> loanDetailInfoPage = loanDetailInfoRepository.findAllByIsDeletedFalseAndLoanStatus(LoanStatus.valueOf(loanStatus), pageable);
        List<LoanDetailInfo> loanDetailInfoList = loanDetailInfoPage.getContent();
        List<String> listCifCodeOfLoan = loanDetailInfoList.stream().map(e -> e.getFinancialInfo().getCifCode()).toList();
        List<CustomerDetailDTO> customerDetailDTOList = customerDubboService.getListCustomerByCifCode(listCifCodeOfLoan);
        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("totalRecord", loanDetailInfoPage.getTotalElements());
        return DataResponseWrapper.builder()
                .data(dataResponse)
                .message("")
                .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                .build();
    }
}
