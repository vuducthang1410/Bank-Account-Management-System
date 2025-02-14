package com.system.account_service.services.impl;

import com.system.account_service.dtos.response.PageDataDTO;
import com.system.account_service.dtos.saving.CreateSavingDTO;
import com.system.account_service.dtos.saving.SavingRp;
import com.system.account_service.entities.AccountDetails;
import com.system.account_service.entities.SavingAccount;
import com.system.account_service.exception.payload.ExistedDataException;
import com.system.account_service.exception.payload.InvalidParamException;
import com.system.account_service.exception.payload.ResourceNotFoundException;
import com.system.account_service.repositories.SavingRepository;
import com.system.account_service.services.AccountDetailService;
import com.system.account_service.services.BankingAccountService;
import com.system.account_service.services.InterestRateService;
import com.system.account_service.services.SavingAccountService;
import com.system.account_service.utils.DateTimeUtils;
import com.system.account_service.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingAccountServiceImpl implements SavingAccountService {
    private final AccountDetailService accountDetailService;
    private final BankingAccountService bankingAccountService;
    private final InterestRateService interestRateService;

    private final SavingRepository repository;

    @Override
    public SavingRp create(CreateSavingDTO data) {
        try {
            AccountDetails accountDetails = accountDetailService.create(data.getAccountDetail());

            String bankingAccountId = data.getBankingAccountId();
            String interestRateId = data.getInterestRateId();

            SavingAccount savingAccount = SavingAccount.builder()
                    .bankingAccount(bankingAccountService.getDataId(bankingAccountId))
                    .accountDetail(accountDetails)
                    .interestRate(interestRateService.findById(interestRateId))
                    .balance(data.getBalance())
                    .endDate(data.getEndDate())
                    .build();

            SavingAccount createdData = repository.save(savingAccount);
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
//    @CachePut(value = "savings", key = "#id")
    public SavingRp updateBalance(String id, BigDecimal balance) {
        SavingAccount savingAccount = repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        savingAccount.setBalance(balance);

        SavingAccount updatedData = repository.save(savingAccount);
        return convertRp(updatedData);
    }

    @Override
//    @CacheEvict(value = "savings", key = "#id")
    public void delete(String id) {
        SavingAccount data = repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        data.setDeleted(true);
        repository.save(data);
    }

    @Override
//    @Cacheable(value = "savings", key = "#id", unless = "#result == null")
    public SavingRp findById(String id) {
        SavingAccount data = repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        return convertRp(data);
    }

    public PageDataDTO<SavingRp> findAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by("createdAt"));
        Page<SavingAccount> pageData = repository.findAllByDeleted(false, pageable);
        List<SavingRp> listData = pageData.stream()
                .map(this::convertRp)
                .toList();

        return PageDataDTO.<SavingRp> builder()
                .total(pageData.getTotalElements())
                .listData(listData)
                .build();
    }

    @Override
    public SavingAccount getDataId(String id) {
        return repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);
    }

    //    Convert sang model response
    private SavingRp convertRp(SavingAccount data) {
        return SavingRp.builder()
                .id(data.getAccountId())
                .bankingAccount(data.getBankingAccount().getAccountId())
                .accountDetail(data.getAccountDetail().getAccountDetailId())
                .interestRate(data.getInterestRate().getInterestRateId())
                .balance(data.getBalance().stripTrailingZeros().toPlainString())
                .endDate(DateTimeUtils.format(DateTimeUtils.DD_MM_YYYY_HH_MM, data.getEndDate()))
                .createAt(DateTimeUtils.format(DateTimeUtils.DD_MM_YYYY_HH_MM, data.getCreatedAt()))
                .build();
    }
}
