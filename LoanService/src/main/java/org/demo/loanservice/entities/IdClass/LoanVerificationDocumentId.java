package org.demo.loanservice.entities.IdClass;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanVerificationDocumentId implements Serializable {
    private String loanDetailInfoId;
    private String legalDocumentsId;
}
