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
    LOAN_AMOUNT_LARGER_LOAN_LIMIT("loan_amount.larger.loan_limit","Loan amount larger than loan limit: loan limit= {}", "30302"),

    DATA_NOT_FOUND("data.notFound", "Data not found", "40400"),
    INTEREST_RATE_NOT_FOUND("interest_rate.not_found", "Interest rate not found", "40401"),
    FORM_DEFT_REPAYMENT_NOT_FOUNT("form_deft_repayment.not_found","Form deft_repayment not found", "40402"),
    LOAN_PRODUCT_NOT_FOUNT("loan_product.not_found", "Loan product not found", "40403"),
    FINANCIAL_INFO_NOT_FOUND("financial_info.not_found", "Financial info not found", "40404"),



    SERVER_ERROR("server.error.message", "Internal server error", "50000");
    private final String keyMessage;
    private final String messageLog;
    private final String code;
    public static final String MESSAGE_LOG="transactionId: {} - {}! RootCause: {}";
}

