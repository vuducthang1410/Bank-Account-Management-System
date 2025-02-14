package com.system.account_service.services.impl;

import com.system.account_service.dtos.credit.CreateCreditDTO;
import com.system.account_service.dtos.credit.CreditRp;
import com.system.account_service.dtos.response.PageDataDTO;
import com.system.account_service.entities.AccountDetails;
import com.system.account_service.entities.CreditAccount;
import com.system.account_service.exception.payload.ExistedDataException;
import com.system.account_service.exception.payload.InvalidParamException;
import com.system.account_service.exception.payload.ResourceNotFoundException;
import com.system.account_service.repositories.CreditRepository;
import com.system.account_service.services.AccountDetailService;
import com.system.account_service.services.BankingAccountService;
import com.system.account_service.services.CreditAccountService;
import com.system.account_service.services.InterestRateService;
import com.system.account_service.utils.DateTimeUtils;
import com.system.account_service.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditAccountServiceImpl implements CreditAccountService {
    private final AccountDetailService accountDetailService;
    private final BankingAccountService bankingAccountService;
    private final InterestRateService interestRateService;

    private final CreditRepository repository;


    @Override
    @Transactional
    public CreditRp create(CreateCreditDTO data) {
        try {
            AccountDetails accountDetails = accountDetailService.create(data.getAccountDetail());

            String bankingAccountId = data.getBankingAccountId();
            String interestRateId = data.getInterestRateId();

            CreditAccount creditAccount = CreditAccount.builder()
                    .bankingAccount(bankingAccountService.getDataId(bankingAccountId))
                    .interestRate(interestRateService.findById(interestRateId))
                    .accountDetail(accountDetails)
                    .creditLimit(data.getCreditLimit())
                    .billingCycle(data.getBillingCycle())
                    .build();

            CreditAccount createdData = repository.save(creditAccount);
            return convertRp(createdData);
        }
        catch (Exception e) {
            if(e instanceof ExistedDataException) {
                throw new ExistedDataException(((ExistedDataException) e).getMsgKey());
            }
            throw new InvalidParamException(MessageKeys.DATA_CREATE_FAILURE);
        }
    }

    @Override
    public CreditRp updateDebtBalance(String id, BigDecimal debtBalance) {
        CreditAccount creditAccount = repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        creditAccount.setDebtBalance(debtBalance);

        CreditAccount updatedData = repository.save(creditAccount);
        return convertRp(updatedData);
    }

    @Override
//    @CacheEvict(value = "credits", key = "#id")
    public void delete(String id) {
        CreditAccount data = repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        data.setDeleted(true);
        repository.save(data);
    }

    @Override
//    @Cacheable(value = "credits", key = "#id", unless = "#result == null")
    public CreditRp findById(String id) {
        CreditAccount data = repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        return convertRp(data);
    }

    @Override
    public PageDataDTO<CreditRp> findAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by("createdAt"));
        Page<CreditAccount> pageData = repository.findAllByDeleted(false, pageable);
        List<CreditRp> listData = pageData.stream()
                .map(this::convertRp)
                .toList();

        return PageDataDTO.<CreditRp> builder()
                .total(pageData.getTotalElements())
                .listData(listData)
                .build();
    }

    @Override
    public CreditAccount getDataId(String id) {
        return repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);
    }

    //    Convert sang model response
    private CreditRp convertRp(CreditAccount data) {
        return CreditRp.builder()
                .id(data.getAccountId())
                .bankingAccount(data.getBankingAccount().getAccountId())
                .accountDetail(data.getAccountDetail().getAccountDetailId())
                .interestRate(data.getInterestRate().getInterestRateId())
                .creditLimit(data.getCreditLimit().stripTrailingZeros().toPlainString())
                .debtBalance(data.getDebtBalance().stripTrailingZeros().toPlainString())
                .billingCycle(data.getBillingCycle().toString())
                .lastPaymentDate(DateTimeUtils.format(DateTimeUtils.DD_MM_YYYY_HH_MM, data.getLastPaymentDate()))
                .createAt(DateTimeUtils.format(DateTimeUtils.DD_MM_YYYY_HH_MM, data.getCreatedAt()))
                .build();
    }
}
