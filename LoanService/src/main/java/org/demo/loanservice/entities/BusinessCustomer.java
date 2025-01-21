package org.demo.loanservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_business_customer")
public class BusinessCustomer  extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "customer_loan_info_id")
    private CustomerLoanInfo customerLoanInfoId;
    private String nameBusiness;
    private String registrationNumber;
    private String taxCode;
}
