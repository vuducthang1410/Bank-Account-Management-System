package org.demo.loanservice.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.demo.loanservice.common.DataResponseWrapper;
import org.demo.loanservice.common.DateUtil;
import org.demo.loanservice.dto.enumDto.DeftRepaymentStatus;
import org.demo.loanservice.dto.enumDto.FormDeftRepaymentEnum;
import org.demo.loanservice.dto.enumDto.PaymentType;
import org.demo.loanservice.dto.request.DeftRepaymentRq;
import org.demo.loanservice.entities.LoanDetailInfo;
import org.demo.loanservice.entities.LoanPenalties;
import org.demo.loanservice.entities.PaymentSchedule;
import org.demo.loanservice.repositories.LoanPenaltiesRepository;
import org.demo.loanservice.repositories.PaymentScheduleRepository;
import org.demo.loanservice.services.IPaymentScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentScheduleServiceImpl implements IPaymentScheduleService {
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final LoanPenaltiesRepository loanPenaltiesRepository;
    private final Logger log = LogManager.getLogger(PaymentScheduleServiceImpl.class);

    @Override
    public void createDeftRepaymentInfo(LoanDetailInfo loanDetailInfo) {
        List<PaymentSchedule> paymentScheduleList = new LinkedList<>();
        log.debug(" form deft repayment : {}", loanDetailInfo.getFormDeftRepayment().name());

        if (loanDetailInfo.getLoanTerm() <= 0) {
            log.warn("Invalid loan term {} for loan account {}. Skipping repayment schedule creation.", loanDetailInfo.getLoanTerm(), loanDetailInfo.getLoanAccountId());
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
            log.debug("Calculated interest amount for loan account {}: {}", loanDetailInfo.getLoanAccountId(), amountInterest.stripTrailingZeros().toPlainString());

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
        // Retrieve the payment schedule based on the provided ID and ensure it is not deleted
        PaymentSchedule paymentSchedule = paymentScheduleRepository.findByIdAndIsDeleted(deftRepaymentRq.getPaymentScheduleId(), false)
                .orElseThrow(() -> new RuntimeException("Payment schedule not found")); // TODO: Handle exception properly

        // Check if the payment has already been completed
        if (paymentSchedule.getIsPaid()) {
            log.info("transactionId:{} - The payment has already been completed - Payment schedule id:{}", transactionId, deftRepaymentRq.getPaymentScheduleId());
            return null;
        }

        // TODO: Replace with actual customer ID retrieval logic
        String customerId = "123456789";

        // Retrieve customer banking account information
//        AccountBankingDTO accountBankingDTO = accountDubboService.getInfoAccountBankingByCustomerId(customerId);

        // Validate if the account is active
//        if (!accountBankingDTO.getStatusAccount().equalsIgnoreCase("ACTIVE")) {
//            log.info("The customer's bank account is not active.");
//            return null;
//        }

        // Get the current account balance
//        BigDecimal bankingAccountBalance = accountBankingDTO.getCurrentBalance();

        BigDecimal bankingAccountBalance = new BigDecimal(10000000);
        log.info("banking balance:{}", bankingAccountBalance.stripTrailingZeros().toPlainString());

        // Check if the account has a positive balance
        if (!(bankingAccountBalance.compareTo(BigDecimal.ZERO) > 0)) {
            log.info("The bank account balance is insufficient.");
            return null;
        }

        log.info("Payment type : {}", deftRepaymentRq.getPaymentType());

        // Handle PRINCIPAL or ALL payment types
        if (deftRepaymentRq.getPaymentType().equalsIgnoreCase(PaymentType.PRINCIPAL.name()) ||
                deftRepaymentRq.getPaymentType().equalsIgnoreCase(PaymentType.ALL.name())) {

            // Ensure sufficient balance to cover the principal amount
            if (!(bankingAccountBalance.compareTo(paymentSchedule.getAmountRepayment()) >= 0)) {
                log.info("Insufficient funds to repay the principal amount.");
                return null;
            }
            // TODO: Call transaction service to process the payment
            if (true) {
                bankingAccountBalance = bankingAccountBalance.subtract(paymentSchedule.getAmountRepayment());
                log.debug("Banking balance after payment loan:{}", bankingAccountBalance.stripTrailingZeros().toPlainString());
                paymentSchedule.setIsPaid(true);
                paymentSchedule.setPaymentScheduleDate(Timestamp.valueOf(LocalDateTime.now()));
            }
        }

        // Handle INTEREST or ALL payment types
        if (deftRepaymentRq.getPaymentType().equalsIgnoreCase(PaymentType.INTEREST.name()) ||
                deftRepaymentRq.getPaymentType().equalsIgnoreCase(PaymentType.ALL.name())) {

            // Ensure sufficient balance to cover the interest amount
            if (!(bankingAccountBalance.compareTo(paymentSchedule.getAmountInterestRate()) >= 0)) {
                log.info("Insufficient funds to repay the interest amount.");
                return null;
            }

            // TODO: Call transaction service to process the payment
            if (true) {
                bankingAccountBalance = bankingAccountBalance.subtract(paymentSchedule.getAmountInterestRate());
                log.debug("Banking balance after payment interest:{}", bankingAccountBalance.stripTrailingZeros().toPlainString());
                paymentSchedule.setPaymentInterestDate(Timestamp.valueOf(LocalDateTime.now()));
                paymentSchedule.setIsPaidInterest(true);
            }
        }

        // Handle PENALTY or ALL payment types (if past due)
        if (deftRepaymentRq.getPaymentType().equalsIgnoreCase(PaymentType.PENALTY.name()) ||
                deftRepaymentRq.getPaymentType().equalsIgnoreCase(PaymentType.ALL.name())) {

            // Check if the payment is overdue by more than 3 days
            if (paymentSchedule.getDueDate().compareTo(DateUtil.getDateAfterNDay(3, paymentSchedule.getDueDate())) > 0) {
                Set<LoanPenalties> loanPenaltiesSet = paymentSchedule.getLoanPenaltiesSet();

                // Calculate the total penalty amount
                BigDecimal totalFineAmount = loanPenaltiesSet.stream()
                        .map(LoanPenalties::getFinedAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Ensure sufficient balance to cover penalties
                if (!(bankingAccountBalance.compareTo(totalFineAmount) >= 0)) {
                    log.info("Insufficient funds to repay penalty fees.");
                    return null;
                }

                // TODO: Call transaction service to process the payment
                if (true) {
                    // Mark penalties as paid and update the payment date
                    Set<LoanPenalties> updatedLoanPenalties = loanPenaltiesSet.stream()
                            .peek(data -> {
                                data.setIsPaid(true);
                                data.setFinedPaymentDate(new Date(System.currentTimeMillis()));
                            })
                            .collect(Collectors.toSet());

                    // Save updated penalty records
                    loanPenaltiesRepository.saveAll(updatedLoanPenalties);
                    paymentSchedule.setLoanPenaltiesSet(updatedLoanPenalties);
                }
            }
        }

        // Save the updated payment schedule
        paymentScheduleRepository.save(paymentSchedule);

        // Return success response
        return DataResponseWrapper.builder()
                .status("00000")
                .data(paymentSchedule.getId())
                .build();
    }

    @Override
    public DataResponseWrapper<Object> getListPaymentScheduleByLoanDetailInfo(String loanInfoId, Integer pageSize, Integer pageNumber, String transactionId) {
        return null;
    }

    public List<PaymentSchedule> getListPaymentScheduleOverdue(Timestamp dueDeftRePaymentDate) {
        return null;
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
        return paymentSchedule;
    }
}
