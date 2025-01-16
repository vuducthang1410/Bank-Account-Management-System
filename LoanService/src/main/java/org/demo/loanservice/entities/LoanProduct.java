package org.demo.loanservice.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_loan_product")
@Audited
@Schema(description =
        "Represents a loan product with details related to loan terms," +
                " interest rates, collateral types, and other relevant features.")
public class LoanProduct extends BaseEntity {

    @Schema(description = "The form of the loan, e.g., consumer loan," +
            " mortgage loan, unsecured loan, etc.")
    private String formLoan;

    @Schema(description = "The loan term, represented in months or years, " +
            "e.g., 12 months, 24 months.")
    private String loanTerm;

    @Schema(description = "The loan limit, indicating the maximum amount " +
            "a customer can borrow for this loan product, e.g., 100 million VND.")
    private String loanLimit;

    @Schema(description = "The target borrower, describing the type of borrower " +
            "the loan product is intended for, e.g., individuals, households, businesses.")
    private String loanObject;

    @Schema(description = "A detailed description of the loan product, including terms and" +
            " features of the loan.")
    private byte[] description;

    @Schema(description = "The interest rate ID associated with this loan product," +
            " linking to the interest rate table.")
    private String interestRateId;

    @Schema(description = "The type of asset that can be used as collateral for " +
            "this loan product, e.g., real estate, vehicles, etc.")
    private String typeAsset;

    @Schema(description = "Utility services associated with the loan product, " +
            "which may include insurance, financial planning services, etc.")
    private byte[] utilities;

    @Schema(description = "The URL to an image representing the loan product," +
            " such as a logo or promotional image.")
    private String productUrlImage;

    @Schema(description = "Loan conditions that borrowers must meet to qualify for" +
            " the loan, such as credit score, income, etc.")
    private byte[] loanCondition;
}

