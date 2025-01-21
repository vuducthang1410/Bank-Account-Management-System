package org.demo.loanservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.demo.loanservice.dto.enumDto.DocumentType;
import org.demo.loanservice.dto.enumDto.RequestStatus;
import org.hibernate.envers.Audited;

import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_legal_documents")
@Audited
public class LegalDocuments extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_loan_info_id")
    private CustomerLoanInfo customerLoanInfoId;
    private String description;
    private String urlDocument;
    @ManyToOne(fetch = FetchType.LAZY)
    private LegalDocuments documentGroupId;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;
    @Temporal(TemporalType.DATE)
    private Date expirationDate;
    private String approvedBy;
}
