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

import java.sql.Date;
@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_loan_collateral")
@Audited
public class LoanCollateral extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "customer_loan_info_id")
    private CustomerLoanInfo customerLoanInfo;
    private String nameAssets;
    private String valueOfProperty;
    private String appraisedValue;
    private Date appraisalDate;
    @ManyToOne
    @JoinColumn(name = "type_mortgaged_assets_id")
    private TypeMortgagedAssets typeMortgagedAssets;
    @ManyToOne
    @JoinColumn(name = "legal_documents_id")
    private LegalDocuments legalDocumentsId;
}
