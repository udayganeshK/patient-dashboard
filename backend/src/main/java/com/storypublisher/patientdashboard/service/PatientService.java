package com.storypublisher.patientdashboard.service;

import com.storypublisher.patientdashboard.dto.PatientDetailsDTO;
import com.storypublisher.patientdashboard.dto.ClaimDTO;
import com.storypublisher.patientdashboard.model.Claim;
import com.storypublisher.patientdashboard.model.Patient;
import com.storypublisher.patientdashboard.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ClaimService claimService;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByFhirId(String fhirId) {
        return patientRepository.findByFhirId(fhirId);
    }

    public Optional<PatientDetailsDTO> getPatientDetails(Long id) {
        Optional<Patient> patientOpt = getPatientById(id);
        if (patientOpt.isEmpty()) return Optional.empty();
        Patient patient = patientOpt.get();
        List<Claim> claims = claimService.getClaimsByPatient(patient);
        List<ClaimDTO> claimDTOs = claims.stream().map(this::toClaimDTO).collect(java.util.stream.Collectors.toList());
        double totalCost = claims.stream().mapToDouble(Claim::getTotalCost).sum();
        PatientDetailsDTO dto = new PatientDetailsDTO();
        dto.setId(patient.getId());
        dto.setFhirId(patient.getFhirId());
        dto.setName(patient.getName());
        dto.setBirthDate(patient.getBirthDate() != null ? patient.getBirthDate().toString() : null);
        dto.setActive(patient.getActive());
        dto.setDeceased(patient.getDeceased());
        dto.setClaims(claimDTOs);
        dto.setTotalCost(totalCost);
        return Optional.of(dto);
    }

    private ClaimDTO toClaimDTO(Claim claim) {
        ClaimDTO dto = new ClaimDTO();
        dto.setId(claim.getId());
        dto.setClaimId(claim.getClaimId());
        dto.setClaimType(claim.getClaimType());
        dto.setServiceStart(claim.getServiceStart());
        dto.setServiceEnd(claim.getServiceEnd());
        dto.setTotalCost(claim.getTotalCost());
        dto.setCurrency(claim.getCurrency());
        dto.setPatientId(claim.getPatient().getId());
        dto.setClaimDescription(claim.getClaimDescription());
        dto.setHospital(claim.getHospital());
        return dto;
    }

    public Page<Patient> searchPatients(String query, String field, Pageable pageable) {
        String trimmedQuery = query != null ? query.trim() : null;
        switch (field) {
            case "fhirId":
                return patientRepository.findByFhirId(trimmedQuery, pageable);
            case "id":
                try {
                    return patientRepository.findById(Long.parseLong(trimmedQuery), pageable);
                } catch (NumberFormatException e) {
                    return Page.empty(pageable);
                }
            case "name":
                return patientRepository.findByNameContainingIgnoreCase(trimmedQuery, pageable);
            default:
                return Page.empty(pageable);
        }
    }
}
