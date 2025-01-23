package org.demo.loanservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.demo.loanservice.dto.enumDto.AssetStatus;
import org.demo.loanservice.dto.enumDto.AssetType;
import org.hibernate.envers.Audited;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_type_mortgaged_assets")
@Audited
public class TypeMortgagedAssets extends BaseEntity{
    private String name;
    private String description;
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    private AssetType assetType;
    @Enumerated(EnumType.STRING)
    private AssetStatus assetStatus;
}
