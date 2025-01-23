package org.demo.loanservice.dto.response;

import lombok.Data;

@Data
public class TypeMortgagedAssetsRp {
    private String id;
    private String name;
    private String description;
    private Boolean isActive;
    private String assetType;
    private String assetStatus;
    private String createdDate;
}
