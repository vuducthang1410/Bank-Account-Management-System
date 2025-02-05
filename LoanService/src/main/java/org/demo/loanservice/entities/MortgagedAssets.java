package org.demo.loanservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @ManyToOne
    @JoinColumn(name = "type_mortgaged_assets_id")
    private TypeMortgagedAssets typeMortgagedAssets;
    @ManyToOne
    @JoinColumn(name = "legal_documents_id")
    private LegalDocuments legalDocumentsId;
    private Boolean isExpired;

    @ManyToOne
    @JoinColumn(name = "financial_info_id")
    private FinancialInfo financialInfo;
}
