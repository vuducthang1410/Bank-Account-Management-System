package org.demo.loanservice.services.impl;

import com.system.common_library.dto.account.CreateLoanDTO;
import com.system.common_library.dto.response.account.AccountInfoDTO;
import com.system.common_library.dto.response.account.LoanAccountInfoDTO;
import com.system.common_library.dto.transaction.loan.CreateLoanDisbursementTransactionDTO;
import com.system.common_library.dto.transaction.loan.TransactionLoanResultDTO;
import com.system.common_library.dto.user.CustomerDetailDTO;
import com.system.common_library.enums.ObjectStatus;
import com.system.common_library.exception.DubboException;
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
import org.demo.loanservice.controllers.exception.DataNotFoundException;
import org.demo.loanservice.controllers.exception.DataNotValidException;
import org.demo.loanservice.controllers.exception.DataNotValidWithConditionException;
import org.demo.loanservice.controllers.exception.ServerErrorException;
import org.demo.loanservice.dto.enumDto.FormDeftRepaymentEnum;
import org.demo.loanservice.dto.enumDto.LoanStatus;
import org.demo.loanservice.dto.enumDto.RequestStatus;
import org.demo.loanservice.dto.enumDto.Unit;
import org.demo.loanservice.dto.projection.LoanAmountInfoProjection;
import org.demo.loanservice.dto.request.IndividualCustomerInfoRq;
import org.demo.loanservice.dto.request.LoanInfoApprovalRq;
import org.demo.loanservice.dto.response.CustomerLoanDetailInfoRp;
import org.demo.loanservice.dto.response.LoanDetailInfoRp;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        CustomerDetailDTO customerInfo = customerDubboService.getCustomerByCifCode(individualCustomerInfoRq.getCifCode());
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
            log.info(MessageData.MESSAGE_LOG, transactionId, String.format(MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getMessageLog(), loanProduct.getLoanLimit().toPlainString()));
            throw new DataNotValidException(MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getKeyMessage(),
                    MessageData.LOAN_AMOUNT_LARGER_LOAN_LIMIT.getCode());
        }

        // Validate if the requested loan term does not exceed the product's term limit
        if (loanProduct.getTermLimit().compareTo(individualCustomerInfoRq.getLoanTerm()) < 0) {
            log.info(MessageData.MESSAGE_LOG, transactionId, String.format(MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getMessageLog(), loanProduct.getTermLimit()));
            throw new DataNotValidException(MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getKeyMessage(),
                    MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getCode());
        }

        Optional<LoanAmountInfoProjection> loanAmountInfoProjectionOptional = loanDetailInfoRepository.getMaxLoanLimitAndCurrentLoanAmount(customerInfo.getCustomerId());
        if (loanAmountInfoProjectionOptional.isEmpty()) {
            log.info(MessageData.MESSAGE_LOG_NOT_FOUND_DATA, transactionId, "Not found information for loan amount and loan amount limit of customer with ", customerInfo.getCustomerId());
            throw new DataNotValidException(MessageData.LOAN_TERM_LARGER_THAN_LIMIT.getKeyMessage(),
                    MessageData.LOAN_LIMIT_AND_TOTAL_LOAN_AMOUNT_NOT_FOUND.getCode());
        }
        LoanAmountInfoProjection loanAmountInfoProjection = loanAmountInfoProjectionOptional.get();
        BigDecimal expectedLoanAmount = individualCustomerInfoRq.getLoanAmount().add(loanAmountInfoProjection.getTotalLoanedAmount());
        if (expectedLoanAmount.compareTo(loanAmountInfoProjection.getLoanAmountMax()) > 0) {
            String messageLog = String.format(MessageData.LOAN_AMOUNT_LARGER_LOAN_REMAINING_LIMIT.getMessageLog(),
                    expectedLoanAmount.toPlainString(),
                    loanAmountInfoProjection.getLoanAmountMax().toPlainString());
            String amountLoanRemainingExpect = financialInfo.getLoanAmountMax().subtract(loanAmountInfoProjection.getTotalLoanedAmount()).stripTrailingZeros().toPlainString();
            log.info(MessageData.MESSAGE_LOG, transactionId, messageLog);

            throw new DataNotValidWithConditionException(MessageData.LOAN_AMOUNT_LARGER_LOAN_REMAINING_LIMIT.getKeyMessage(),
                    MessageData.LOAN_AMOUNT_LARGER_LOAN_REMAINING_LIMIT.getCode(),
                    amountLoanRemainingExpect
            );
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
            if (bankingAccountDTO == null || !StringUtils.hasText(bankingAccountDTO.getAccountNumber())) {
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
                log.error(MessageData.MESSAGE_LOG_DETAIL, transactionId, MessageData.DATA_RESPONSE_TRANSACTION_SERVICE_NOT_VALID.getMessageLog(), "Data response is null");
                throw new DataNotValidException(MessageData.SERVER_ERROR.getKeyMessage(), MessageData.DATA_RESPONSE_TRANSACTION_SERVICE_NOT_VALID.getCode());
            }
            //handler response from transaction service
            loanDetailInfo.setRequestStatus(RequestStatus.valueOf(loanInfoApprovalRq.getRequestStatus()));
            loanDetailInfo.setNote(loanDetailInfo.getNote());
            loanDetailInfo.setLoanStatus(LoanStatus.ACTIVE);

            //create disbursement info history
            DisbursementInfoHistory disbursementInfoHistory = getDisbursementInfoHistory(loanDetailInfo, loanAccountInfoDTO, transactionResponse);
            disbursementInfoHistoryRepository.saveAndFlush(disbursementInfoHistory);
            loanDetailInfoRepository.saveAndFlush(loanDetailInfo);
            //generate deft repayment schedule
            paymentScheduleService.createDeftRepaymentInfo(loanDetailInfo);

            //todo: call notification service

        } catch (Exception e) {
            log.info(MessageData.MESSAGE_LOG, transactionId, e.getMessage());
            //todo: callback transaction
            throw new ServerErrorException();

        }
        return DataResponseWrapper.builder()
                .data(loanDetailInfo.getId())
                .message("")
                .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                .build();
    }


    @Override
    public DataResponseWrapper<Object> getAllByLoanStatus(String loanStatus, Integer pageNumber, Integer pageSize, String transactionId) {
        log.debug("Loan status : {}", loanStatus);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate"));
        Page<LoanDetailInfo> loanDetailInfoPage = loanDetailInfoRepository.findAllByIsDeletedFalseAndLoanStatus(LoanStatus.valueOf(loanStatus), pageable);
        List<LoanDetailInfo> loanDetailInfoList = loanDetailInfoPage.getContent();
        log.debug("size of loan detail info list :{}", loanDetailInfoList.size());
        if (loanDetailInfoList.isEmpty()) {
            return DataResponseWrapper.builder()
                    .data(Collections.emptyList())
                    .message(MessageData.FIND_SUCCESSFULLY.getMessageLog())
                    .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                    .build();
        }
        List<String> listCifCodeOfLoan = loanDetailInfoList.stream().map(e -> e.getFinancialInfo().getCifCode()).toList();
        try {
            //Get customer info by customer id from dubbo service
            List<CustomerDetailDTO> customerDetailDTOList = customerDubboService.getListCustomerByCifCode(listCifCodeOfLoan);
            log.debug("Size of customer detail dto list : {}", customerDetailDTOList.size());
            // Convert list to map
            Map<String, CustomerDetailDTO> customerDetailDTOMap = customerDetailDTOList.stream()
                    .collect(Collectors.toMap(
                            CustomerDetailDTO::getCustomerId,
                            Function.identity(),
                            (existing, replacement) -> existing // (void duplicate key issue)Keep the existing entry in case of duplicate
                    ));
            //convert to response to dto response
            List<CustomerLoanDetailInfoRp> customerLoanDetailInfoRpList = loanDetailInfoList
                    .stream()
                    .map(e -> {
                        CustomerDetailDTO customerDetailDTO = customerDetailDTOMap.get(e.getFinancialInfo().getCustomerId());
                        return mapObjectToCustomerLoanDetailInfoDto(e, customerDetailDTO);
                    })
                    .toList();
            Map<String, Object> dataResponse = new HashMap<>();
            dataResponse.put("totalRecord", loanDetailInfoPage.getTotalElements());
            dataResponse.put("dataResponse", customerLoanDetailInfoRpList);
            return DataResponseWrapper.builder()
                    .data(dataResponse)
                    .message(MessageData.FIND_SUCCESSFULLY.getMessageLog())
                    .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                    .build();
        } catch (DubboException de) {
            log.error(MessageData.MESSAGE_LOG_DETAIL, transactionId, "Execute error while get all loan detail by loan status ", de.getMessage());
            throw de;
        } catch (Exception e) {
            log.error(MessageData.MESSAGE_LOG_DETAIL, transactionId, "Execute error while get all loan detail by loan status ", e.getMessage(), e);
            throw new ServerErrorException();
        }
    }

    @Override
    public DataResponseWrapper<Object> getAllByCustomerId(Integer pageNumber, Integer pageSize, String transactionId, String customerId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        List<FinancialInfo> financialInfoList = financialInfoService.getListFinancialInfoByCustomerId(customerId, transactionId);
        log.debug("Size of financial info list : {}", financialInfoList.size());
        if (financialInfoList.isEmpty()) {
            log.info("not found financial info so can not find loan detail info");
            throw new DataNotFoundException(MessageData.FINANCIAL_INFO_NOT_FOUND.getKeyMessage(), MessageData.FINANCIAL_INFO_NOT_FOUND.getCode());
        }
        List<String> financialInfoIsList = financialInfoList.stream().map(FinancialInfo::getId).toList();
        Page<LoanDetailInfo> loanDetailInfoPage = loanDetailInfoRepository.findAllByIsDeletedFalseAndFinancialInfo_IdIn(financialInfoIsList, pageable);
        List<LoanDetailInfo> loanDetailInfoList = loanDetailInfoPage.getContent();
        log.debug("Total element expect : {}", loanDetailInfoPage.getTotalElements());
        log.debug("Page size - {} - page number - {} - number of records retrieved - {}", pageSize, pageNumber, loanDetailInfoList.size());
        //convert entity to response
        List<LoanDetailInfoRp> loanDetailInfoRpList = loanDetailInfoList.stream().map(this::mapObjectToLoanDetailInfoRp).toList();
        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("totalRecord", loanDetailInfoPage.getTotalElements());
        dataResponse.put("loanDetailInfoRpList", loanDetailInfoRpList);
        return DataResponseWrapper.builder()
                .data(dataResponse)
                .message(MessageData.FIND_SUCCESSFULLY.getMessageLog())
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
        disbursementInfoHistory.setLoanDate(DateUtil.getDateAfterNDay(1));
        disbursementInfoHistory.setDouDate(DateUtil.getDateAfterNMonths(loanDetailInfo.getLoanTerm()));
        return disbursementInfoHistory;
    }

    private CustomerLoanDetailInfoRp mapObjectToCustomerLoanDetailInfoDto(LoanDetailInfo loanDetailInfo, CustomerDetailDTO customerDetailDTO) {
        CustomerLoanDetailInfoRp customerLoanDetailInfoRp = new CustomerLoanDetailInfoRp();
        customerLoanDetailInfoRp.setCustomerId(customerDetailDTO.getCustomerId());
        customerLoanDetailInfoRp.setFormDeftRepayment(loanDetailInfo.getFormDeftRepayment().name());
        customerLoanDetailInfoRp.setFullName(customerDetailDTO.getFullName());
        customerLoanDetailInfoRp.setIdentityCard(customerDetailDTO.getIdentityCard());
        customerLoanDetailInfoRp.setInterestRate(loanDetailInfo.getInterestRate());
        customerLoanDetailInfoRp.setLoanAmount(loanDetailInfo.getLoanAmount().toPlainString());
        customerLoanDetailInfoRp.setLoanDetailInfoId(loanDetailInfo.getId());
        customerLoanDetailInfoRp.setLoanProductName(loanDetailInfo.getLoanProductId().getNameProduct());
        customerLoanDetailInfoRp.setLoanTerm(loanDetailInfo.getLoanTerm());
        customerLoanDetailInfoRp.setPhone(customerDetailDTO.getPhone());
        customerLoanDetailInfoRp.setUnit(loanDetailInfo.getUnit().name());
        customerLoanDetailInfoRp.setCreatedTime(DateUtil.format(DateUtil.DD_MM_YYY_HH_MM_SLASH, loanDetailInfo.getCreatedDate()));
        return customerLoanDetailInfoRp;
    }

    private LoanDetailInfoRp mapObjectToLoanDetailInfoRp(LoanDetailInfo loanDetailInfo) {
        LoanDetailInfoRp loanDetailInfoRp = new LoanDetailInfoRp();
        loanDetailInfoRp.setLoanDetailInfoId(loanDetailInfo.getId());
        loanDetailInfoRp.setLoanAmount(loanDetailInfo.getLoanAmount().toPlainString());
        loanDetailInfoRp.setLoanProductName(loanDetailInfo.getLoanProductId().getNameProduct());
        loanDetailInfoRp.setLoanTerm(loanDetailInfo.getLoanTerm());
        loanDetailInfoRp.setUnit(loanDetailInfo.getUnit().name());
        loanDetailInfoRp.setInterestRate(loanDetailInfo.getInterestRate());
        loanDetailInfoRp.setCreatedDate(DateUtil.format(DateUtil.DD_MM_YYY_HH_MM_SLASH, loanDetailInfo.getCreatedDate()));
        DisbursementInfoHistory disbursementInfoHistory = loanDetailInfo.getDisbursementInfoHistory();
        if (disbursementInfoHistory != null) {
            loanDetailInfoRp.setDouDate(DateUtil.format(DateUtil.DD_MM_YYY_HH_MM_SLASH, disbursementInfoHistory.getDouDate()));
            loanDetailInfoRp.setDateDisbursement(DateUtil.format(DateUtil.DD_MM_YYY_HH_MM_SLASH, disbursementInfoHistory.getLoanDate()));
        }

        return loanDetailInfoRp;
    }
}
