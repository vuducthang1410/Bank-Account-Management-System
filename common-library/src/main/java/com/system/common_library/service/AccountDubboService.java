package com.system.common_library.service;

import com.system.common_library.dto.account.*;
import com.system.common_library.dto.report.AccountReportRequest;
import com.system.common_library.dto.report.AccountReportResponse;
import com.system.common_library.dto.response.account.AccountInfoDTO;
import com.system.common_library.dto.response.account.LoanAccountInfoDTO;
import com.system.common_library.enums.AccountType;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDubboService {

    AccountInfoDTO getAccountDetail(String account);
    AccountInfoDTO getBankingAccount(String cifCode);
    AccountInfoDTO getLoanAccountDTO(String loanAccountId);
    AccountInfoDTO getAccountsByCifCode(String cifCode);
    List<AccountInfoDTO> getAccountsByBranchId(String branchId, List<AccountType> type);

    /* Todo: Create BankingAccount (Payment)
         CustomerService call this method */
    AccountInfoDTO createBankingAccount(CreateDubboBankingDTO data);

    AccountInfoDTO createSavingAccount(String customerId, CreateSavingDTO data);
    AccountInfoDTO createCreditAccount(String customerId, CreateCreditDTO data);

    /* Todo: Create LoanAccount (Loan)
         LoanService call this method */
    LoanAccountInfoDTO createLoanAccount(String customerId, CreateLoanDTO data);

    Boolean updateBalance(String account, BigDecimal amount);
    AccountInfoDTO updateAccountInfo(String account, UpdateAccountDTO data);
    AccountInfoDTO updateAccountStatus();

    //gRPC for reporting
    void getReportCreditAccount(String account);
    AccountReportResponse getReportAccount(String account);
    List<AccountReportResponse> getReportAccounts(AccountReportRequest request);
}
