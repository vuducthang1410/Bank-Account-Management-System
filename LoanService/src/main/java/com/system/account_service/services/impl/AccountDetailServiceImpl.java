package com.system.account_service.services.impl;

import com.system.account_service.dtos.account_detail.CreateAccountDetailDTO;
import com.system.account_service.dtos.response.PageDataDTO;
import com.system.account_service.entities.AccountDetails;
import com.system.account_service.entities.type.AccountStatus;
import com.system.account_service.entities.type.AccountTypes;
import com.system.account_service.exception.payload.ExistedDataException;
import com.system.account_service.exception.payload.ResourceNotFoundException;
import com.system.account_service.repositories.AccountDetailRepository;
import com.system.account_service.services.AccountDetailService;
import com.system.account_service.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountDetailServiceImpl implements AccountDetailService {
    private final AccountDetailRepository repository;

    @Override
    public AccountDetails create(CreateAccountDetailDTO data) {
        Boolean existedAccountNumber = repository.existsByAccountNumber(data.getAccountNumber());
        if(existedAccountNumber){
            throw new ExistedDataException(MessageKeys.EXISTED_ACCOUNT_NUMBER);
        }

        AccountDetails accountDetails = AccountDetails.builder()
                .customerId(data.getCustomerId())
                .accountNumber(data.getAccountNumber())
                .accountType(AccountTypes.valueOf(data.getAccountType()))
//                .branchName(data.getBranchName())
                .status(AccountStatus.valueOf(data.getStatus()))
                .build();

        return repository.save(accountDetails);
    }

    @Override
//    @CachePut(value = "account_detail", key = "#id")
    public AccountDetails updateStatus(String id, String status) {
        AccountDetails accountDetails = repository.findAccountDetailsByAccountDetailIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        accountDetails.setStatus(AccountStatus.valueOf(status));

        return repository.save(accountDetails);
    }

    @Override
//    @CacheEvict(value = "account_detail", key = "#id")
    public void delete(String id) {
        AccountDetails data = repository.findAccountDetailsByAccountDetailIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);

        data.setDeleted(true);
        repository.save(data);
    }

    @Override
//    @Cacheable(value = "account_detail", key = "#id", unless = "#result == null")
    public AccountDetails findById(String id) {
        return repository.findAccountDetailsByAccountDetailIdAndDeleted(id, false)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public PageDataDTO<AccountDetails> findAll(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by("createdAt"));
        Page<AccountDetails> pageData = repository.findAllByDeleted(false, pageable);
        List<AccountDetails> listData = pageData.stream().toList();

        return PageDataDTO.<AccountDetails> builder()
                .total(pageData.getTotalElements())
                .listData(listData)
                .build();
    }
}
