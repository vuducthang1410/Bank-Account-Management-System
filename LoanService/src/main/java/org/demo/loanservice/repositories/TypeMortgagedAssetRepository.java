package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.TypeMortgagedAssets;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeMortgagedAssetRepository extends JpaRepository<TypeMortgagedAssets,String> {
    Page<TypeMortgagedAssets> getAllByIsDeleted(Boolean isDeleted, Pageable pageable);
    Optional<TypeMortgagedAssets> getTypeMortgagedAssetsByIdAndIsDeleted(String id,Boolean isDeleted);
}
