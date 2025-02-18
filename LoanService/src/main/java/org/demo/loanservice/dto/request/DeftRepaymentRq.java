package org.demo.loanservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeftRepaymentRq {
    private String paymentScheduleId;
    private String paymentType;
}
