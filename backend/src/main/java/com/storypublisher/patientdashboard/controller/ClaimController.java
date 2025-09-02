package com.storypublisher.patientdashboard.controller;

import com.storypublisher.patientdashboard.dto.ClaimDTO;
import com.storypublisher.patientdashboard.dto.ClaimTypeCountDTO;
import com.storypublisher.patientdashboard.model.Claim;
import com.storypublisher.patientdashboard.model.Patient;
import com.storypublisher.patientdashboard.service.ClaimService;
import com.storypublisher.patientdashboard.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {
    @Autowired
    private ClaimService claimService;
    @Autowired
    private PatientService patientService;

    @GetMapping
    public List<ClaimDTO> getAllClaims() {
        return claimService.getAllClaims().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ClaimDTO>> getClaimsByPatient(@PathVariable Long patientId) {
        Optional<Patient> patientOpt = patientService.getPatientById(patientId);
        if (patientOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ClaimDTO> claims = claimService.getClaimsByPatient(patientOpt.get()).stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getClaimSummary() {
        return ResponseEntity.ok(claimService.getClaimSummary());
    }

    @GetMapping("/type-breakdown")
    public List<ClaimTypeCountDTO> getClaimTypeBreakdown() {
        return claimService.getClaimTypeBreakdown();
    }

    private ClaimDTO toDTO(Claim claim) {
        ClaimDTO dto = new ClaimDTO();
        dto.setId(claim.getId());
        dto.setClaimId(claim.getClaimId());
        dto.setClaimType(claim.getClaimType());
        dto.setServiceStart(claim.getServiceStart());
        dto.setServiceEnd(claim.getServiceEnd());
        dto.setTotalCost(claim.getTotalCost());
        dto.setCurrency(claim.getCurrency());
        dto.setPatientId(claim.getPatient().getId());
        return dto;
    }
}
