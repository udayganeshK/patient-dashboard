package com.storypublisher.patientdashboard.repository;

import com.storypublisher.patientdashboard.model.Claim;
import com.storypublisher.patientdashboard.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByPatient(Patient patient);
    List<Claim> findByClaimType(String claimType);

    @Query("SELECT COUNT(c), COALESCE(SUM(c.totalCost),0) FROM Claim c")
    Object getClaimSummary();

    @Query("SELECT COALESCE(c.claimType, 'Unknown'), COUNT(c) FROM Claim c GROUP BY c.claimType")
    List<Object[]> getClaimTypeBreakdown();
}
