package com.storypublisher.patientdashboard.service;

import com.storypublisher.patientdashboard.dto.ClaimTypeCountDTO;
import com.storypublisher.patientdashboard.model.Claim;
import com.storypublisher.patientdashboard.model.Patient;
import com.storypublisher.patientdashboard.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClaimService {
    @Autowired
    private ClaimRepository claimRepository;

    public List<Claim> getClaimsByPatient(Patient patient) {
        return claimRepository.findByPatient(patient);
    }

    public List<Claim> getClaimsByType(String claimType) {
        return claimRepository.findByClaimType(claimType);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Map<String, Object> getClaimSummary() {
        Object[] row = (Object[]) claimRepository.getClaimSummary();
        Map<String, Object> summary = new HashMap<>();
        summary.put("claimCount", ((Number) row[0]).longValue());
        summary.put("totalCost", ((Number) row[1]).doubleValue());
        return summary;
    }

    public List<ClaimTypeCountDTO> getClaimTypeBreakdown() {
        List<Object[]> results = claimRepository.getClaimTypeBreakdown();
        List<ClaimTypeCountDTO> breakdown = new java.util.ArrayList<>();
        for (Object[] row : results) {
            String type = (String) row[0];
            long count = ((Number) row[1]).longValue();
            breakdown.add(new ClaimTypeCountDTO(type, count));
        }
        return breakdown;
    }

    public Double getTotalCostByPatient(Patient patient) {
        List<Claim> claims = claimRepository.findByPatient(patient);
        return claims.stream().mapToDouble(Claim::getTotalCost).sum();
    }
}
