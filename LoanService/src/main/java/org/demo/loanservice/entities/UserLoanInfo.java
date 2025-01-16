package org.demo.loanservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user_loan_info")
@Audited
public class UserLoanInfo extends BaseEntity{
    private String customerId;
    private String formDeftRepaymentId;
    private String loanProductId;
    private Short status;
    private Timestamp loanDate;

}
