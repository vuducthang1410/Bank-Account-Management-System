package org.demo.loanservice.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanProduct extends BaseEntity {
    private String formLoan;
    private String loanTerm;
    private String loanLimit;
}
