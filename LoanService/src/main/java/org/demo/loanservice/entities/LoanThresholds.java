package org.demo.loanservice.entities;

import jakarta.persistence.Entity;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_loan_thresholds")
@Audited
public class LoanThresholds extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "loan_product_id")
    private LoanProduct loanProduct;
    @ManyToOne
    @JoinColumn(name = "type_mortgaged_assets")
    private TypeMortgagedAssets typeMortgagedAssets;

    private Double maxLoanPercentage;
}
