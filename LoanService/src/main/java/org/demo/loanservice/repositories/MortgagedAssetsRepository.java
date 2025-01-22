package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.MortgagedAssets;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MortgagedAssetsRepository extends JpaRepository<MortgagedAssets, String> {
}
