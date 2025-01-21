package org.demo.loanservice.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.demo.loanservice.dto.enumDto.Unit;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_financial_info")
public class FinancialInfo extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "customer_loan_info_id")
    private CustomerLoanInfo customerLoanInfoId;
    private String income;
    @Enumerated(EnumType.STRING)
    private Unit unit;
    @Schema(description = "The user's credit score", example = "750")
    private Integer creditScore;
    @Schema(description = "Source of income", example = "Salary")
    private String incomeSource;
    @Schema(description = "Type of income, e.g., regular, irregular", example = "Regular")
    private String incomeType;
}
