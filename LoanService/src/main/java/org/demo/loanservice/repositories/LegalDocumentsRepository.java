package org.demo.loanservice.repositories;

import org.demo.loanservice.entities.LegalDocuments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalDocumentsRepository extends JpaRepository<LegalDocuments, String> {
}
