package org.demo.loanservice.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.demo.loanservice.dto.enumDto.Unit;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_interest_rate")
@Audited
public class InterestRate extends BaseEntity {
    private BigDecimal interestRate;
    @Enumerated(EnumType.STRING)
    private Unit unit;
    private Timestamp dateActive;
    private Boolean isActive;
    private BigDecimal minimumAmount;
    private Integer minimumLoanTerm;
    @OneToMany(mappedBy = "interestRate")
    private Set<LoanProduct> loanProducts;
}
