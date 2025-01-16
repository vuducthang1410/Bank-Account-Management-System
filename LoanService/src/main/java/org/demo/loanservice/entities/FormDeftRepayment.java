package org.demo.loanservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_form_deft_repayment")
@Audited
public class FormDeftRepayment extends BaseEntity {
    private String formName;
    @OneToMany(mappedBy = "formDeftRepaymentId")
    private Set<UserLoanInfo> userLoanInfos;
}
