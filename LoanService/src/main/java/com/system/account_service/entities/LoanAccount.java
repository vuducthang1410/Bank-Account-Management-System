package com.system.account_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_accounts")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String accountId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_detail_id", nullable = false)
    private AccountDetails accountDetail;

    @Column(nullable = false)
    private BigDecimal balance;

    @PrePersist
    protected void onCreate() {
        if(balance == null) {
            balance = BigDecimal.ZERO;
        }
    }
}
