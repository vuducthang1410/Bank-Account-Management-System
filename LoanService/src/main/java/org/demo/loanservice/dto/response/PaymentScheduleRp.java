package org.demo.loanservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentScheduleRp {
    private String paymentScheduleId;
    private String nameSchedule;
    private String dueDate;
    private String amountRemaining;
    private String status;
    private String isPaid;
}
