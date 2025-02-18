package org.demo.loanservice.services.impl;


import com.system.common_library.dto.response.account.AccountInfoDTO;
import com.system.common_library.dto.transaction.loan.CreateLoanPaymentTransactionDTO;
import com.system.common_library.dto.transaction.loan.TransactionLoanResultDTO;
import com.system.common_library.enums.ObjectStatus;
import com.system.common_library.exception.DubboException;
import com.system.common_library.service.AccountDubboService;
import com.system.common_library.service.TransactionDubboService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.common.MessageData;
import org.demo.loanservice.common.MessageValue;
import org.demo.loanservice.common.Util;
import org.demo.loanservice.controllers.exception.DataNotFoundException;
import org.demo.loanservice.controllers.exception.DataNotValidException;
import org.demo.loanservice.controllers.exception.ServerErrorException;
import org.demo.loanservice.dto.TransactionInfoDto;
import org.demo.loanservice.dto.enumDto.DeftRepaymentStatus;
import org.demo.loanservice.dto.enumDto.FormDeftRepaymentEnum;
import org.demo.loanservice.dto.enumDto.PaymentType;
import org.demo.loanservice.dto.projection.RepaymentScheduleProjection;
import org.demo.loanservice.dto.request.DeftRepaymentRq;
import org.demo.loanservice.dto.response.PaymentScheduleRp;
import org.demo.loanservice.entities.LoanDetailInfo;
import org.demo.loanservice.entities.LoanPenalties;
import org.demo.loanservice.entities.PaymentSchedule;
import org.demo.loanservice.entities.RepaymentHistory;
import org.demo.loanservice.repositories.LoanPenaltiesRepository;
import org.demo.loanservice.repositories.PaymentScheduleRepository;
import org.demo.loanservice.repositories.RepaymentHistoryRepository;
import org.demo.loanservice.services.ILoanDetailRepaymentScheduleService;
import org.demo.loanservice.services.IPaymentScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaymentScheduleServiceImpl implements IPaymentScheduleService {
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final LoanPenaltiesRepository loanPenaltiesRepository;
    private final ILoanDetailRepaymentScheduleService loanDetailRepaymentScheduleService;
    private final RepaymentHistoryRepository repaymentHistoryRepository;

    private final Util util;
    @DubboReference
    private AccountDubboService accountDubboService;
    @DubboReference
    private TransactionDubboService transactionDubboService;
    private final Logger log = LogManager.getLogger(PaymentScheduleServiceImpl.class);

    @Override
    public void createDeftRepaymentInfo(LoanDetailInfo loanDetailInfo) {
        List<PaymentSchedule> paymentScheduleList = new LinkedList<>();
        log.debug(" form deft repayment : {}", loanDetailInfo.getFormDeftRepayment().name());

        if (loanDetailInfo.getLoanTerm() <= 0) {
            log.warn("Invalid loan term {} for loan account {}. Skipping repayment schedule creation.", loanDetailInfo.getLoanTerm(), loanDetailInfo.getDisbursementInfoHistory().getLoanAccountId());
            return;
        }
        /*
            loan amount repayment every term = loan amount / loan term
         */
        BigDecimal amountRepaymentEveryTerm = loanDetailInfo.getLoanAmount()
                .divide(new BigDecimal(loanDetailInfo.getLoanTerm()), RoundingMode.HALF_UP);
        log.debug("amount repayment every term : {}", amountRepaymentEveryTerm.stripTrailingZeros().toPlainString());
        if (loanDetailInfo.getFormDeftRepayment().equals(FormDeftRepaymentEnum.PRINCIPAL_AND_INTEREST_MONTHLY)) {
            /*
                amount interest = loan amount * interest rate/100
             */
            BigDecimal amountInterest = loanDetailInfo.getLoanAmount().multiply(BigDecimal.valueOf(loanDetailInfo.getInterestRate()))
                    .divide(new BigDecimal(100), RoundingMode.HALF_UP);
            log.debug("Calculated interest amount for loan account {}: {}", loanDetailInfo.getDisbursementInfoHistory().getLoanAccountId(), amountInterest.stripTrailingZeros().toPlainString());

            for (int i = 0; i < loanDetailInfo.getLoanTerm(); i++) {
                PaymentSchedule paymentSchedule = createPaymentSchedule(loanDetailInfo, amountRepaymentEveryTerm, amountInterest, i);
                paymentScheduleList.add(paymentSchedule);
            }
        } else if (loanDetailInfo.getFormDeftRepayment().equals(FormDeftRepaymentEnum.PRINCIPAL_INTEREST_DECREASING)) {
            for (int i = 0; i < loanDetailInfo.getLoanTerm(); i++) {
               /*
                   amount interest = remain loan amount * interest rate/100
                */
                BigDecimal remainingAmount = amountRepaymentEveryTerm.multiply(new BigDecimal(loanDetailInfo.getLoanTerm() - i));
                BigDecimal amountInterest = remainingAmount
                        .multiply(BigDecimal.valueOf(loanDetailInfo.getInterestRate()))
                        .divide(new BigDecimal(100), RoundingMode.HALF_UP);
                log.debug("Term :{} - remain amount : {} - interest amount: {}", i, remainingAmount.toPlainString(), amountInterest.toPlainString());

                PaymentSchedule paymentSchedule = createPaymentSchedule(loanDetailInfo, amountRepaymentEveryTerm, amountInterest, i);
                paymentScheduleList.add(paymentSchedule);
            }
        }
        paymentScheduleRepository.saveAll(paymentScheduleList);
    }

    @Override
    @Transactional
    public DataResponseWrapper<Object> automaticallyRepaymentDeftPeriodically(DeftRepaymentRq deftRepaymentRq, String transactionId) {
        // Retrieve the payment schedule and ensure it exists
        PaymentSchedule paymentSchedule = paymentScheduleRepository.findByIdAndIsDeleted(deftRepaymentRq.getPaymentScheduleId(), false)
                .orElseThrow(() -> {
                    log.warn("Transaction {}: Payment schedule with ID {} not found.", transactionId, deftRepaymentRq.getPaymentScheduleId());
                    return new DataNotFoundException(MessageData.PAYMENT_SCHEDULE_NOT_FOUND.getKeyMessage(),
                            MessageData.PAYMENT_SCHEDULE_NOT_FOUND.getCode());
                });

        // Validate if the payment has already been completed
        if (paymentSchedule.getIsPaid()) {
            log.warn("Transaction {}: Payment already completed for schedule ID {}", transactionId, deftRepaymentRq.getPaymentScheduleId());
            throw new DataNotValidException(MessageData.PAYMENT_SCHEDULE_COMPLETED.getKeyMessage(),
                    MessageData.PAYMENT_SCHEDULE_COMPLETED.getCode());
        }

        try {
            // Fetch customer banking and loan account details
            log.info("Transaction {}: Fetching banking account and loan account details.", transactionId);
            AccountInfoDTO accountBankingDTO = accountDubboService.getBankingAccount(paymentSchedule.getLoanDetailInfo().getFinancialInfo().getCifCode());
            AccountInfoDTO accountLoanInfoDTO = accountDubboService.getLoanAccountDTO(paymentSchedule.getLoanDetailInfo().getDisbursementInfoHistory().getLoanAccountId());

            // Ensure the banking account is active before proceeding
            if (!ObjectStatus.ACTIVE.equals(accountBankingDTO.getStatusAccount())) {
                log.warn("Transaction {}: Banking account with CIF code {} is not active (Status: {}).",
                        transactionId, paymentSchedule.getLoanDetailInfo().getFinancialInfo().getCifCode(), accountBankingDTO.getStatusAccount());
                throw new DataNotValidException(MessageData.BANKING_ACCOUNT_NOT_ACTIVE.getKeyMessage(),
                        MessageData.BANKING_ACCOUNT_NOT_ACTIVE.getCode());
            }

            // Check the available balance in the banking account
            TransactionInfoDto transactionInfoDto = new TransactionInfoDto();
            transactionInfoDto.setTotalPayment(accountBankingDTO.getCurrentAccountBalance());
            log.info("Transaction {}: Current account balance is {}", transactionId,
                    transactionInfoDto.getBalanceRemaining().stripTrailingZeros().toPlainString());

            // Validate if the account has sufficient funds
            if (transactionInfoDto.getBalanceRemaining().compareTo(BigDecimal.ZERO) < 0) {
                log.warn("Transaction {}: Insufficient balance in the banking account.", transactionId);
                throw new DataNotValidException(MessageData.ACCOUNT_BALANCE_NOT_ENOUGH.getKeyMessage(),
                        MessageData.ACCOUNT_BALANCE_NOT_ENOUGH.getCode());
            }

            // Initialize repayment history tracking
            List<RepaymentHistory> repaymentHistoryList = new LinkedList<>();

            // Process the payment based on the specified payment type (INTEREST, PRINCIPAL, PENALTY)
            log.info("Transaction {}: Initiating payment processing for schedule ID {}.", transactionId, deftRepaymentRq.getPaymentScheduleId());
            processPayment(deftRepaymentRq.getPaymentType(), paymentSchedule, accountBankingDTO, accountLoanInfoDTO, transactionInfoDto, repaymentHistoryList);

            // Verify if the payment was processed successfully
            if (transactionInfoDto.getTotalPayment().compareTo(BigDecimal.ZERO) == 0) {
                log.warn("Transaction {}: No payment was processed due to insufficient funds or invalid payment.", transactionId);
                throw new DataNotValidException(MessageData.ACCOUNT_BALANCE_NOT_ENOUGH.getKeyMessage(),
                        MessageData.ACCOUNT_BALANCE_NOT_ENOUGH.getCode());
            }

            // Persist the updated payment schedule and repayment history
            paymentScheduleRepository.save(paymentSchedule);
            repaymentHistoryRepository.saveAll(repaymentHistoryList);
            log.info("Transaction {}: Payment successfully processed and saved.", transactionId);

            return DataResponseWrapper.builder()
                    .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                    .data(paymentSchedule.getId())
                    .build();

        } catch (DubboException dubboException) {
            log.error("Transaction {}: Error during payment processing - {}", transactionId, dubboException.getMessage(), dubboException);
            throw new ServerErrorException();
        } catch (Exception e) {
            log.error("Transaction {}: Unexpected error - {}", transactionId, e.getMessage(), e);
            throw new DataNotValidException("Unexpected error during repayment process.", "500");
        }
    }

    /**
     * Process the payment based on the type (INTEREST, PRINCIPAL, PENALTY, ALL).
     */
    private void processPayment(String paymentType, PaymentSchedule paymentSchedule, AccountInfoDTO accountBankingDTO,
                                AccountInfoDTO accountLoanInfoDTO, TransactionInfoDto transactionInfoDto, List<RepaymentHistory> repaymentHistoryList) {
        // Process interest payment
        if (shouldProcess(paymentType, PaymentType.INTEREST)) {
            try {
                paymentLoan(paymentSchedule, accountBankingDTO, accountLoanInfoDTO, transactionInfoDto, repaymentHistoryList);
            } catch (Exception e) {
                log.error("Error processing interest payment: {}", e.getMessage());
                throw e;
            }
        }

        // Process principal payment
        if (shouldProcess(paymentType, PaymentType.PRINCIPAL)) {
            try {
                paymentInterest(paymentSchedule, accountBankingDTO, accountLoanInfoDTO, transactionInfoDto, repaymentHistoryList);
            } catch (Exception e) {
                log.error("Error processing principal payment: {}", e.getMessage());
                throw e;
            }
        }

        // Process penalty payment if overdue
        if (shouldProcess(paymentType, PaymentType.PENALTY) && isPaymentOverdue(paymentSchedule)) {
            try {
                paymentPenalty(paymentSchedule, accountBankingDTO, accountLoanInfoDTO, transactionInfoDto, repaymentHistoryList);
            } catch (Exception e) {
                log.error("Error processing penalty payment: {}", e.getMessage());
                throw e;
            }
        }
    }

    /**
     * Check if the payment schedule is overdue by more than 3 days.
     */
    private boolean isPaymentOverdue(PaymentSchedule paymentSchedule) {
        return LocalDate.now().isAfter(paymentSchedule.getDueDate().toLocalDateTime().toLocalDate().plusDays(3));
    }

    private boolean shouldProcess(String requestType, PaymentType type) {
        return requestType.equalsIgnoreCase(type.name()) || requestType.equalsIgnoreCase(PaymentType.ALL.name());
    }

    private void paymentLoan(PaymentSchedule paymentSchedule,
                             AccountInfoDTO accountBankingDTO,
                             AccountInfoDTO accountLoanInfoDTO,
                             TransactionInfoDto transactionInfoDto,
                             List<RepaymentHistory> repaymentHistoryList) {
        TransactionLoanResultDTO transactionLoanResultDTO = processPayment(paymentSchedule, accountBankingDTO, accountLoanInfoDTO, paymentSchedule.getAmountInterestRate(), PaymentType.INTEREST.name());
        transactionInfoDto.setBalanceRemaining(transactionLoanResultDTO.getBalanceBankingAccount());
        transactionInfoDto.setTotalPayment(transactionInfoDto.getTotalPayment().add(paymentSchedule.getAmountInterestRate()));
        log.debug("Banking balance after payment loan:{}", transactionInfoDto.getBalanceRemaining().stripTrailingZeros().toPlainString());
        log.debug("Total amount transaction after payment loan:{}", transactionInfoDto.getTotalPayment().toPlainString());
        paymentSchedule.setPaymentInterestDate(Timestamp.valueOf(LocalDateTime.now()));
        paymentSchedule.setIsPaidInterest(true);
        createRepaymentHistory(repaymentHistoryList, transactionLoanResultDTO, paymentSchedule.getAmountRepayment(), PaymentType.PRINCIPAL.name(), PaymentType.PRINCIPAL, paymentSchedule);
    }

    private void paymentInterest(PaymentSchedule paymentSchedule,
                                 AccountInfoDTO accountBankingDTO,
                                 AccountInfoDTO accountLoanInfoDTO,
                                 TransactionInfoDto transactionInfoDto,
                                 List<RepaymentHistory> repaymentHistoryList) {
        TransactionLoanResultDTO transactionLoanResultDTO = processPayment(paymentSchedule, accountBankingDTO, accountLoanInfoDTO, paymentSchedule.getAmountRepayment(), PaymentType.PRINCIPAL.name());
        transactionInfoDto.setBalanceRemaining(transactionLoanResultDTO.getBalanceBankingAccount());
        transactionInfoDto.setTotalPayment(transactionInfoDto.getTotalPayment().add(paymentSchedule.getAmountRepayment()));
        log.debug("Banking balance after payment interest:{}", transactionInfoDto.getBalanceRemaining().stripTrailingZeros().toPlainString());
        log.debug("Total amount transaction after payment interest:{}", transactionInfoDto.getTotalPayment().toPlainString());
        //update info payment schedule
        paymentSchedule.setIsPaid(true);
        paymentSchedule.setPaymentScheduleDate(Timestamp.valueOf(LocalDateTime.now()));
        createRepaymentHistory(repaymentHistoryList, transactionLoanResultDTO, paymentSchedule.getAmountInterestRate(), "", PaymentType.INTEREST, paymentSchedule);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void paymentPenalty(PaymentSchedule paymentSchedule,
                                AccountInfoDTO accountBankingDTO,
                                AccountInfoDTO accountLoanInfoDTO,
                                TransactionInfoDto transactionInfoDto,
                                List<RepaymentHistory> repaymentHistoryList) {

        Set<LoanPenalties> loanPenaltiesSet = paymentSchedule.getLoanPenaltiesSet();

        // Calculate the total penalty amount
        BigDecimal totalFineAmount = loanPenaltiesSet.stream()
                .filter(lp -> !lp.getIsPaid())
                .map(LoanPenalties::getFinedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        TransactionLoanResultDTO transactionLoanResultDTO = processPayment(paymentSchedule, accountBankingDTO, accountLoanInfoDTO, totalFineAmount, PaymentType.PENALTY.name());
        transactionInfoDto.setTotalPayment(transactionInfoDto.getTotalPayment().add(totalFineAmount));
        log.debug("Banking balance after payment penalty:{}", transactionInfoDto.getBalanceRemaining().stripTrailingZeros().toPlainString());
        log.debug("Total amount transaction after payment penalty: {}", transactionInfoDto.getTotalPayment().toPlainString());
        // Mark penalties as paid and update the payment date
        for (LoanPenalties penalty : loanPenaltiesSet) {
            penalty.setIsPaid(true);
            penalty.setFinedPaymentDate(new Date(System.currentTimeMillis()));
        }
        // Save updated penalty records
        loanPenaltiesRepository.saveAll(loanPenaltiesSet);
        paymentSchedule.setLoanPenaltiesSet(loanPenaltiesSet);
        createRepaymentHistory(repaymentHistoryList, transactionLoanResultDTO, totalFineAmount, "", PaymentType.PENALTY, paymentSchedule);
    }

    @Override
    public DataResponseWrapper<Object> getListPaymentScheduleByLoanDetailInfo(String loanInfoId, Integer pageSize, Integer pageNumber, String transactionId) {
        LoanDetailInfo loanDetailInfo = loanDetailRepaymentScheduleService.getLoanDetailInfoById(loanInfoId, transactionId);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<RepaymentScheduleProjection> repaymentScheduleProjectionPage =
                paymentScheduleRepository.findPaymentScheduleByLoanDetailInfoId(loanDetailInfo.getId(), pageable);

        List<PaymentScheduleRp> paymentScheduleRpList = repaymentScheduleProjectionPage
                .getContent()
                .stream()
                .map(this::mapToPaymentScheduleRp)
                .toList();

        Map<String, Object> dataResponse = new HashMap<>();
        dataResponse.put("totalRecord", repaymentScheduleProjectionPage.getTotalElements());
        dataResponse.put("listPaymentSchedule", paymentScheduleRpList);
        return DataResponseWrapper.builder()
                .data(dataResponse)
                .status(MessageValue.STATUS_CODE_SUCCESSFULLY)
                .message(util.getMessageFromMessageSource(MessageData.FIND_SUCCESSFULLY.getKeyMessage()))
                .build();
    }

    private PaymentSchedule createPaymentSchedule(LoanDetailInfo loanDetailInfo, BigDecimal amountRepaymentEveryTerm, BigDecimal amountInterestRate, int index) {
        PaymentSchedule paymentSchedule = new PaymentSchedule();
        paymentSchedule.setLoanDetailInfo(loanDetailInfo);
        paymentSchedule.setStatus(DeftRepaymentStatus.NOT_DUE);
        paymentSchedule.setAmountRepayment(amountRepaymentEveryTerm);
        paymentSchedule.setAmountInterestRate(amountInterestRate);
        paymentSchedule.setIsPaid(false);
        paymentSchedule.setIsDeleted(false);
        paymentSchedule.setIsPaidInterest(false);
        paymentSchedule.setDueDate(DateUtil.getDateAfterNMonths(index + 1));
        paymentSchedule.setName(String.valueOf((index + 1)));
        return paymentSchedule;
    }


    private TransactionLoanResultDTO processPayment(PaymentSchedule paymentSchedule,
                                                    AccountInfoDTO accountBankingDTO,
                                                    AccountInfoDTO accountLoanInfoDTO,
                                                    BigDecimal amount,
                                                    String paymentType) {
        // Ensure sufficient balance to cover the principal amount
        if (accountBankingDTO.getCurrentAccountBalance().compareTo(amount) < 0) {
            log.info("Insufficient funds to repay the principal amount.");
            throw new DataNotValidException(MessageData.ACCOUNT_BALANCE_NOT_ENOUGH.getKeyMessage(),
                    MessageData.ACCOUNT_BALANCE_NOT_ENOUGH.getCode());
        }
        String typeTransaction = paymentType.equals(PaymentType.PRINCIPAL.name()) ? MessageValue.CONTENT_TRANSACTION_PRINCIPAL :
                (paymentType.equals(PaymentType.INTEREST.name()) ? MessageValue.CONTENT_TRANSACTION_INTEREST : MessageValue.CONTENT_TRANSACTION_PENALTY);
        String description = util.getMessageTransactionFromMessageSource(
                typeTransaction,
                paymentSchedule.getLoanDetailInfo().getLoanProductId().getNameProduct(),
                paymentSchedule.getName(),
                amount.stripTrailingZeros().toPlainString());

        // Call transaction service to process the payment
        CreateLoanPaymentTransactionDTO paymentLoanDTO = CreateLoanPaymentTransactionDTO.builder()
                .note(paymentType)
                .amount(amount)
                .cifCode(paymentSchedule.getLoanDetailInfo().getFinancialInfo().getCifCode())
                .isCashPayment(false)
                .description(description)
                .paymentAccount(accountBankingDTO.getAccountNumber())
                .loanAccount(accountLoanInfoDTO.getAccountNumber())
                .build();
        return transactionDubboService.createLoanPaymentTransaction(paymentLoanDTO);
    }

    private void createRepaymentHistory(List<RepaymentHistory> repaymentHistoryList,
                                        TransactionLoanResultDTO transactionLoanResultDTO,
                                        BigDecimal amountPayment,
                                        String note,
                                        PaymentType paymentType,
                                        PaymentSchedule paymentSchedule
    ) {
        RepaymentHistory repaymentHistory = new RepaymentHistory();
        repaymentHistory.setTransactionId(transactionLoanResultDTO.getTransactionId());
        repaymentHistory.setPaymentSchedule(paymentSchedule);
        repaymentHistory.setAmountPayment(amountPayment);
        repaymentHistory.setPaymentType(paymentType);
        repaymentHistory.setNote(note);
        repaymentHistoryList.add(repaymentHistory);
    }

    private PaymentScheduleRp mapToPaymentScheduleRp(RepaymentScheduleProjection repaymentScheduleProjection) {
        PaymentScheduleRp paymentScheduleRp = new PaymentScheduleRp();
        paymentScheduleRp.setPaymentScheduleId(repaymentScheduleProjection.getId());
        paymentScheduleRp.setNameSchedule(repaymentScheduleProjection.getName());
        paymentScheduleRp.setStatus(repaymentScheduleProjection.getStatus());
        paymentScheduleRp.setDueDate(DateUtil.format(DateUtil.DD_MM_YYYY_SLASH, new Date(repaymentScheduleProjection.getDueDate().getTime())));
        BigDecimal amountRemaining = repaymentScheduleProjection.getAmountFinedRemaining()
                .add(repaymentScheduleProjection.getPaymentInterestRate() == null ? repaymentScheduleProjection.getAmountInterest() : BigDecimal.ZERO)
                .add(repaymentScheduleProjection.getPaymentScheduleDate() == null ? repaymentScheduleProjection.getAmountRepayment() : BigDecimal.ZERO);
        paymentScheduleRp.setAmountRemaining(amountRemaining.stripTrailingZeros().toPlainString());
        return paymentScheduleRp;
    }
}
