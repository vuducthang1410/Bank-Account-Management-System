package org.demo.loanservice.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.demo.loanservice.dto.enumDto.DeftRepaymentStatus;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_deft_repayment_history")
@Audited
public class DeftRepaymentHistory extends BaseEntity{
    private String paymentDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_loan_info_id")
    private CustomerLoanInfo customerLoanInfoId;

    private Timestamp dueDate;
    private BigDecimal amountRepayment;
    private Boolean isPaid;
    @Enumerated(EnumType.STRING)
    private DeftRepaymentStatus status;

}
