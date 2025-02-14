package com.system.account_service.dtos.saving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingRp implements Serializable {
    private String id;
    private String bankingAccount;
    private String accountDetail;
    private String balance;
    private String interestRate;
    private String endDate;
    private String createAt;
}
