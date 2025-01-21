package org.demo.loanservice.entities;

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
import org.demo.loanservice.dto.enumDto.AssetType;
import org.hibernate.envers.Audited;

import java.sql.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Table(name = "tbl_mortgaged_assets")
public class MortgagedAssets extends BaseEntity {
    private String nameAssets;
    private String valueOfProperty;
    private String appraisedValue;
    private Date appraisalDate;
    @Enumerated(EnumType.STRING)
    private AssetType assetType;
    @ManyToOne
    @JoinColumn(name = "customer_loan_Info_id")
    private CustomerLoanInfo customerLoanInfoId;
    @ManyToOne
    @JoinColumn(name = "legal_documents_id")
    private LegalDocuments legalDocumentsId;
    private Boolean isExpired;
}
