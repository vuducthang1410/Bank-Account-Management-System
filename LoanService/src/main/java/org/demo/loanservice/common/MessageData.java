package org.demo.loanservice.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageData {

    MISSING_PARAMETER("missing.parameter", "Missing parameter", "40001"),
    VALID_DTO_NAME_NOT_BLANK("valid.dto.name.notBlank", "DTO name must not be blank", "40002"),
    VALID_DTO_DESCRIPTION_NOT_BLANK("valid.dto.description.notBlank", "DTO description must not be blank", "40003"),
    VALID_DTO_UNIT_NOT_VALID("valid.dto.unit.notValid", "DTO unit is not valid", "40004"),
    VALID_DTO_INTEREST_RATE_IS_POSITIVE("valid.dto.interest_rate.isPositive", "Interest rate must be positive", "40005"),
    MISSING_PARAMETER_IN_HEADER("missing.parameter.header", "Missing parameter in header", "40006"),
    INVALID_DATA("invalid.data", "Data not valid", "40000"),


    CREATED_SUCCESSFUL("created.successful", "Created successfully", "20000"),
    DELETED_SUCCESSFUL("deleted.successful", "Deleted successfully", "20000"),
    FIND_SUCCESSFULLY("findObject.success", "Find object successfully", "20000"),


    FINANCIAL_INFO_NOT_APPROVE("financial_info.not_approve", "Financial info not approve", "30301"),
    LOAN_AMOUNT_LARGER_LOAN_LIMIT("loan_amount.larger.loan_limit", "Loan amount larger than loan amount limit: loan amount limit= {}", "30302"),
    LOAN_TERM_LARGER_THAN_LIMIT("loan_term.larger.loan_term_limit", "Loan term larger than loan term limit: loan term limit= {}", "30303"),
    CUSTOMER_ACCOUNT_NOT_ACTIVE("customer_account.not_active", "Customer account is not active", "30304"),
    BANKING_ACCOUNT_NOT_ACTIVE("banking_account.not_active", "Banking account is not active", "30305"),
    CREATED_LOAN_ACCOUNT_ERROR("created.loan_account.error", "Created loan account is error", "30306"),
    ACCOUNT_BALANCE_NOT_ENOUGH("account_balance.not_enough", "Account balance is not enough to make transactions", "30307"),
    PAYMENT_SCHEDULE_COMPLETED("payment_schedule.completed", "The payment has already been completed", "30308"),
    BANKING_ACCOUNT_NOT_EXITS("banking_account.not_exits", "Execute error while get banking account.", "30309"),

    DATA_NOT_FOUND("data.notFound", "Data not found", "40400"),
    INTEREST_RATE_NOT_FOUND("interest_rate.not_found", "Interest rate not found", "40401"),
    LOAN_PRODUCT_NOT_FOUNT("loan_product.not_found", "Loan product not found", "40403"),
    FINANCIAL_INFO_NOT_FOUND("financial_info.not_found", "Financial info not found", "40404"),
    LOAN_DETAIL_INFO_NOT_FOUND("loan_detail_info.not_found", "Loan detail info not found", "40405"),
    CUSTOMER_ACCOUNT_NOT_FOUND("customer_account.not_found", "Customer account not found", "40406"),
    INTEREST_RATE_VALID_NOT_FOUND("interest_rate.valid.not_found", "Not found interest rate valid::condition = {}", "40406"),
    PAYMENT_SCHEDULE_NOT_FOUND("payment_schedule.not_found", "Not found payment schedule valid", "40407"),


    SERVER_ERROR("server.error.message", "Internal server error", "50000");
    private final String keyMessage;
    private final String messageLog;
    private final String code;
    public static final String MESSAGE_LOG = "transactionId: {} - RootCause: {}";
    public static final String MESSAGE_LOG_NOT_FOUND_DATA = "transactionId: {} - RootCause:: {} - id::{}";
}

