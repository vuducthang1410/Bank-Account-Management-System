package org.demo.loanservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.demo.loanservice.entities.IdClass.LoanVerificationDocumentId;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
@Table(name = "loan_verification_documents")
public class LoanVerificationDocument {
    @EmbeddedId
    private LoanVerificationDocumentId id;
    @MapsId("customerLoanInfoId")
    @ManyToOne
    @JoinColumn(name = "customer_loan_info_id")
    private CustomerLoanInfo customerLoanInfo;

    @MapsId("legalDocumentsId")
    @ManyToOne
    @JoinColumn(name = "legal_document_id")
    private LegalDocuments legalDocuments;

    @CreatedBy
    @Column(updatable = false, nullable = false, length = 50, name = "CREATED_BY")
    @Audited
    private String createdBy;
    @LastModifiedBy
    @Column(length = 50,name = "LAST_MODIFIED_BY")
    @Audited
    private String lastModifiedBy;
    @CreatedDate
    @Column(updatable = false, nullable = false, length = 50, name = "CREATED_DATE")
    @Audited
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column( length = 50,name = "LAST_MODIFIED_DATE")
    @Audited
    private LocalDateTime lastModifiedDate;
    @Column( length = 1,name = "IS_DELETED")
    @Audited
    private Boolean isDeleted;
}
