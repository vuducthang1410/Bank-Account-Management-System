package org.demo.loanservice.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_deft_repayment_history")
@Schema(description = "Deft repayment history")
@Audited
public class DeftRepaymentHistory extends BaseEntity{
    private String paymentDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_loan_info_id")
    private CustomerLoanInfo customerLoanInfoId;
}
