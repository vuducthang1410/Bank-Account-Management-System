package org.demo.loanservice.dto.enumDto;

public enum FormDeftRepaymentEnum {
    /**
     * Pay interest monthly, principal is paid quarterly.
     * - Each month: The borrower only pays the interest on the loan.
     * - Every quarter (every 3 months): The borrower pays a portion of the principal along with that month's interest.
     * - Suitable for borrowers with cyclical income, reducing monthly financial pressure.
     */
//    PRINCIPAL_QUARTERLY_INTEREST_MONTHLY,

    /**
     * Monthly repayment of both principal and interest, with interest calculated on the remaining loan balance.
     * - Each month: The borrower pays a portion of the principal and interest calculated based on the remaining loan balance.
     * - Interest decreases over time as the loan balance is gradually reduced.
     * - Helps reduce total interest payments but has higher initial repayment amounts.
     */
    PRINCIPAL_INTEREST_DECREASING,

    /**
     * Fixed monthly repayment of both principal and interest, with interest calculated on the initial loan amount.
     * - Each month: The borrower pays a fixed amount, consisting of a portion of the principal and a fixed interest amount.
     * - Total monthly payment remains unchanged throughout the loan period.
     * - Suitable for borrowers who prefer stable and predictable repayment plans.
     */
    PRINCIPAL_AND_INTEREST_MONTHLY;
}

