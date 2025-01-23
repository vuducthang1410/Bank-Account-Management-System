package org.demo.loanservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request object for creating or updating a mortgaged asset type")
public class TypeMortgagedAssetsRq {

    @Schema(description = "The name of the mortgaged asset", example = "Car")
    private String name;

    @Schema(description = "A description of the mortgaged asset type", example = "A vehicle used as collateral for a loan")
    private String description;

    @Schema(description = "The status of the mortgaged asset (e.g., active, inactive)", example = "  one in group(NEW,PARTIALLY_LIQUIDATED,IN_USE,ON_SALE")
    private String status;

    @Schema(description = "The type of asset, such as 'property', 'vehicle', etc.", example = "one in group(DEPRECIABLE,NON_DEPRECIABLE,RISKY)")
    private String typeAssets;
}
