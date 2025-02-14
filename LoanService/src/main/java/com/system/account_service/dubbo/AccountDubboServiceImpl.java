package com.system.account_service.dubbo;

import com.system.account_service.services.BankingAccountService;
import com.system.common_library.dto.account.*;
import com.system.common_library.dto.report.AccountReportDTO;
import com.system.common_library.dto.report.AccountReportRequestDTO;
import com.system.common_library.dto.response.account.AccountInfoDTO;
import com.system.common_library.dto.response.account.LoanAccountInfoDTO;
import com.system.common_library.enums.AccountType;
import com.system.common_library.service.AccountDubboService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@DubboService
@RequiredArgsConstructor
public class AccountDubboServiceImpl implements AccountDubboService {
    private final BankingAccountService bankingService;

    @Override
    public void getAccountDetail(String accountId) {

    }

    @Override
    public AccountInfoDTO getBankingAccount(String cifCode) {

        return null;
    }

    @Override
    public AccountInfoDTO getLoanAccountDTO(String loanAccountId) {
        return null;
    }

    @Override
    public void getAccountsByCifCode(String cifCode) {

    }

    @Override
    public List<AccountBranchDTO> getAccountsByBranchId(String branchId, List<AccountType> type) {

        List<AccountBranchDTO> result = new ArrayList<>();

        if(type.contains(AccountType.PAYMENT)){

            /// Get banking account
            /// Add result
        }

        if(type.contains(AccountType.SAVINGS)){

            /// Get savings account
            /// Add result
        }

        if(type.contains(AccountType.LOAN)){

            /// Get loan account
            /// Add result
        }

        if(type.contains(AccountType.CREDIT)){

            /// Get credit account
            /// Add result
        }

        return result;
    }

    @Override
    public void createBankingAccount(String customerId, CreateBankingDTO data) {

    }

    @Override
    public void createSavingAccount(String customerId, CreateSavingDTO data) {

    }

    @Override
    public void createCreditAccount(String customerId, CreateCreditDTO data) {

    }

    @Override
    public LoanAccountInfoDTO createLoanAccount(String customerId, CreateLoanDTO data) {

        return null;
    }

    @Override
    public void updateBalance(String accountId, BigDecimal balance) {

    }

    @Override
    public void updateAccountInfo(String accountId, UpdateAccountDTO data) {

    }

    @Override
    public void getReportAccount(String account, LocalDate startDate, LocalDate endDate) {

    }

    @Override
    public void getReportAccounts(String branchId, LocalDate startDate, LocalDate endDate) {

    }

    @Override
    public void updateAccountStatus() {

    }

    @Override
    public AccountReportDTO getReportAccount(String account) {
        return null;
    }

    @Override
    public List<AccountReportDTO> getReportAccounts(AccountReportRequestDTO request) {
        return List.of();
    }
}
