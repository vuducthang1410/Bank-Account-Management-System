package org.demo.loanservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_preferential_interest_rate")
@Audited
public class PreferentialInterestRate extends BaseEntity{
    private Integer termPreferential;
    private Double preferentialInterestRates;
    private Integer minimumTerm;
    private Boolean isActive;
}
