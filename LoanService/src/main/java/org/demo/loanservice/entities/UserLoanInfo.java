package org.demo.loanservice.entities;

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

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user_loan_info")
@Audited
public class UserLoanInfo extends BaseEntity{
    private String customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="form_deft_repayment_id")
    private FormDeftRepayment formDeftRepaymentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_product_id")
    private LoanProduct loanProductId;
    private Short status;
    private Timestamp loanDate;

}
