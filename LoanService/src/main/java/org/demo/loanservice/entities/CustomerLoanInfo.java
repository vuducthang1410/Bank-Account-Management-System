package org.demo.loanservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.demo.loanservice.dto.enumDto.LoanStatus;
import org.demo.loanservice.dto.enumDto.RequestStatus;
import org.demo.loanservice.dto.enumDto.Unit;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_customer_loan_info")
@Audited
public class CustomerLoanInfo extends BaseEntity {
    private String customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_deft_repayment_id")
    private FormDeftRepayment formDeftRepaymentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_product_id")
    private LoanProduct loanProductId;
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;
    private Timestamp loanDate;
    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;
    private BigDecimal loanAmount;
    private Integer loanTerm;
    @Enumerated(EnumType.STRING)
    private Unit unit;
    private String reasonRefuse;

    private Double interestRate;
    @ManyToOne
    @JoinColumn(name = "preferential_interest_rate_id")
    private PreferentialInterestRate preferentialInterestRate;
    @OneToMany(mappedBy = "customerLoanInfoId")
    private Set<DeftRepaymentHistory> deftRepaymentHistories;

    // todo: Persist financial information
    private Integer creditScore;
    private String income;
    private String incomeSource;
    private String incomeType;
    private String debtStatus;
    private Timestamp lastUpdatedCreditReview;

}
