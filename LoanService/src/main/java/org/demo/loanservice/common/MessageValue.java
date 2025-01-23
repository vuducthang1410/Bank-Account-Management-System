package org.demo.loanservice.common;

public class MessageValue {

    // Common error messages
    public static final String INVALID_DATA = "invalid.data";
    public static final String CREATED_SUCCESSFUL = "created.successful";

     public static final String FIND_SUCCESSFULLY="findObject.success";
     public static final String DATA_NOT_FOUND="data.notFound";
     public static final String INTEREST_RATE_NOT_FOUND="interest_rate.not_found";
     public static final String MISSING_PARAMETER="missing.parameter";
    // Validation messages for DTOs
    public static final String VALID_DTO_NAME_NOT_BLANK = "valid.dto.name.notBlank";
    public static final String VALID_DTO_DESCRIPTION_NOT_BLANK = "valid.dto.description.notBlank";
    public static final String VALID_DTO_UNIT_NOT_VALID = "valid.dto.unit.notValid";
    public static final String VALID_DTO_INTEREST_RATE_IS_POSITIVE="valid.dto.interest_rate.isPositive";
    public static final String MISSING_PARAMETER_IN_HEADER="missing.parameter.header";
    public static final String SERVER_ERROR="server.error.message";
    public static final String TYPE_MORTGAGED_ASSET_NOT_FOUND="type_mortgaged_asset.not_found";
}
