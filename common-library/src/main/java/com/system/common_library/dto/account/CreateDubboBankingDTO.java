package com.system.common_library.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDubboBankingDTO {
    private String customerId;
    private String cifCode;
    private String fullName;
    private String phone;
    private String email;
    private String branchId;
}
