package com.system.account_service.services.impl;

import com.system.account_service.dtos.banking.BankingRp;
import com.system.account_service.dtos.banking.CreateBankingDTO;
import com.system.account_service.dtos.response.PageDataDTO;
import com.system.account_service.entities.AccountDetails;
import com.system.account_service.entities.BankingAccount;
import com.system.account_service.exception.payload.ExistedDataException;
import com.system.account_service.exception.payload.InvalidParamException;
import com.system.account_service.exception.payload.ResourceNotFoundException;
import com.system.account_service.repositories.BankingRepository;
import com.system.account_service.services.AccountDetailService;
import com.system.account_service.services.BankingAccountService;
import com.system.account_service.utils.DateTimeUtils;
import com.system.account_service.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankingAccountServiceImpl implements BankingAccountService {
    private final AccountDetailService accountDetailService;

    private final BankingRepository repository;


    @Override
    @Transactional
    public BankingRp create(CreateBankingDTO data) {
        try {
            AccountDetails accountDetails = accountDetailService.create(data.getAccountDetail());

            BankingAccount bankingAccount = BankingAccount.builder()
                    .nickName(data.getNickName())
                    .accountDetail(accountDetails)
                    .build();

            BankingAccount createdData = repository.save(bankingAccount);
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
//    @CachePut(value = "payments", key = "#id")
    public BankingRp updateBalance(String id, BigDecimal balance) {
        BankingAccount bankingAccount = repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        bankingAccount.setBalance(balance);

        BankingAccount updatedData = repository.save(bankingAccount);
        return convertRp(updatedData);
    }

    @Override
//    @CacheEvict(value = "payments", key = "#id")
    public void delete(String id) {
        BankingAccount data = repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        data.setDeleted(true);
        repository.save(data);
    }

    @Override
//    @Cacheable(value = "payments", key = "#id", unless = "#result == null")
    public BankingRp findById(String id) {
        BankingAccount data = repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        return convertRp(data);
    }

    @Override
    public PageDataDTO<BankingRp> findAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by("createdAt"));
        Page<BankingAccount> pageData = repository.findAllByDeleted(false, pageable);
        List<BankingRp> listData = pageData.stream()
                .map(this::convertRp)
                .toList();

        return PageDataDTO.<BankingRp> builder()
                .total(pageData.getTotalElements())
                .listData(listData)
                .build();
    }

    @Override
    public BankingAccount getDataId(String id) {
        return repository.findByAccountIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);
    }


//    Convert sang model response
    private BankingRp convertRp(BankingAccount data) {
        return BankingRp.builder()
                .id(data.getAccountId())
                .nickName(data.getNickName())
                .accountDetail(data.getAccountDetail().getAccountDetailId())
                .balance(data.getBalance().stripTrailingZeros().toPlainString())
                .createAt(DateTimeUtils.format(DateTimeUtils.DD_MM_YYYY_HH_MM, data.getCreatedAt()))
                .build();
    }
}
