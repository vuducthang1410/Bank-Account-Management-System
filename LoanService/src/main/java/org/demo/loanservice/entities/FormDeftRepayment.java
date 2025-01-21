package org.demo.loanservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.demo.loanservice.dto.enumDto.FormDeftRepaymentEnum;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_form_deft_repayment")
public class FormDeftRepayment extends BaseEntity{
    private String name;
    @Enumerated(EnumType.STRING)
    private FormDeftRepaymentEnum code;
    private String description;
    private Boolean isActive;
}
