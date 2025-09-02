package com.storypublisher.patientdashboard.repository;

import com.storypublisher.patientdashboard.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByFhirId(String fhirId);
    Page<Patient> findByFhirId(String fhirId, Pageable pageable);
    Page<Patient> findById(Long id, Pageable pageable);
    Page<Patient> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
