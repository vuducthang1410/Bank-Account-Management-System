package com.system.common_library.service;

import com.system.common_library.dto.report.TransactionReportDTO;
import com.system.common_library.dto.report.TransactionReportRequest;
import com.system.common_library.dto.report.TransactionReportResponse;
import com.system.common_library.dto.response.PagedDTO;
import com.system.common_library.dto.transaction.TransactionDTO;
import com.system.common_library.dto.transaction.TransactionExtraDTO;
import com.system.common_library.dto.transaction.account.credit.CreateCreditDisbursementTransactionDTO;
import com.system.common_library.dto.transaction.account.credit.CreateCreditPaymentTransactionDTO;
import com.system.common_library.dto.transaction.account.credit.CreateCreditTransactionDTO;
import com.system.common_library.dto.transaction.account.savings.CreateSavingsDisbursementTransactionDTO;
import com.system.common_library.dto.transaction.account.savings.CreateSavingsPaymentTransactionDTO;
import com.system.common_library.dto.transaction.account.savings.CreateSavingsTransactionDTO;
import com.system.common_library.dto.transaction.loan.CreateLoanDisbursementTransactionDTO;
import com.system.common_library.dto.transaction.loan.CreateLoanPaymentTransactionDTO;
import com.system.common_library.dto.transaction.loan.CreateLoanTransactionDTO;
import com.system.common_library.dto.transaction.loan.TransactionLoanResultDTO;
import com.system.common_library.exception.DubboException;

import java.time.LocalDate;
import java.util.List;

public interface TransactionDubboService {

    // Get transaction detail (Lấy thông tin chi tiết giao dịch)
    TransactionExtraDTO getTransactionDetail(String id) throws DubboException;

    // Get transaction list by account number (Lấy lịch sử giao dịch theo số tài khoản)
    PagedDTO<TransactionDTO> getTransactionListByAccount(String account, int page, int limit) throws DubboException;

    // Get transaction list by CIF code (Lấy lịch sử giao dịch theo mã CIF)
    PagedDTO<TransactionDTO> getTransactionListByCIF(String cif, int page, int limit) throws DubboException;

    // Loan account disbursement (Giải ngân cho tài khoản vay)
    TransactionLoanResultDTO createLoanAccountDisbursement(CreateLoanDisbursementTransactionDTO create) throws DubboException;

    // Interest/penalty transaction for loan account (Tính lãi suất/tiền phạt cho tài khoản vay)
    TransactionLoanResultDTO createLoanTransaction(CreateLoanTransactionDTO create) throws DubboException;

    // Loan account payment (Thanh toán cho tài khoản vay)
    TransactionLoanResultDTO createLoanPaymentTransaction(CreateLoanPaymentTransactionDTO create) throws DubboException;

    // Savings account payment (Gửi tiền vào tài khoản tiết kiệm)
    boolean createSavingsPaymentTransaction(CreateSavingsDisbursementTransactionDTO create) throws DubboException;

    // Interest transaction for savings account (Tính lãi suất cho tài khoản tiết kiệm)
    boolean createSavingTransaction(CreateSavingsTransactionDTO create) throws DubboException;

    // Closing savings account (Tất toán tài khoản tiết kiệm)
    boolean createSavingsClosingTransaction(CreateSavingsPaymentTransactionDTO create) throws DubboException;

    // Credit account disbursement (Giải ngân cho tài khoản tín dụng)
    boolean createCreditAccountDisbursement(CreateCreditDisbursementTransactionDTO create) throws DubboException;

    // Interest transaction for credit account (Tính lãi suất cho tài khoản tín dụng)
    boolean createCreditTransaction(CreateCreditTransactionDTO create) throws DubboException;

    // Credit account payment (Thanh toán cho tài khoản tín dụng)
    boolean createCreditPaymentTransaction(CreateCreditPaymentTransactionDTO create) throws DubboException;

    // gRPC for report
    // Get transaction account stats for report (Lấy các thông số giao dịch một tài khoản (Report service))
    TransactionReportResponse getTransactionStatByAccountNumber(String account, LocalDate startDate, LocalDate endDate)
            throws DubboException;

    // Get transaction by field filter(Lấy danh sách các giai dịch theo điều kiện)
    List<TransactionReportDTO> getTransactionByFilter(TransactionReportRequest request) throws DubboException;
}
