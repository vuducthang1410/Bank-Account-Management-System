package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.UnsecuredAssets;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnsecuredAssetsRepository extends JpaRepository<UnsecuredAssets, String> {
}
