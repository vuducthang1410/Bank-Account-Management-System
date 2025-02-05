package org.demo.loanservice.dto.request;

import lombok.Data;

@Data
public class IndividualCustomerInfoRq {
    private String identificationNumber;
    private String loanProductId;

}
