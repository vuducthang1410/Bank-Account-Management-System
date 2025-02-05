package org.demo.loanservice.entities;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.demo.loanservice.dto.enumDto.ApplicableObjects;
import org.demo.loanservice.dto.enumDto.Unit;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_financial_info")
public class FinancialInfo extends BaseEntity{
    @Column(name = "customer_id")
    private String  customerId;
    private BigDecimal income;
    @Enumerated(EnumType.STRING)
    private Unit unit;
    @Schema(description = "The user's credit score", example = "750")
    private Integer creditScore;
    @Schema(description = "Source of income", example = "Salary")
    private String incomeSource;
    @Schema(description = "Type of income, e.g., regular, irregular", example = "Regular")
    private String incomeType;
    @Enumerated(EnumType.STRING)
    private ApplicableObjects applicableObjects;
    private Timestamp lastUpdatedCreditReview;
    private String debtStatus;
    private Date expiredDate;
    private Boolean isExpired;
    private Boolean isApproved;
}
