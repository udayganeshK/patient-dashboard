package com.storypublisher.patientdashboard.controller;

import com.storypublisher.patientdashboard.dto.PatientDTO;
import com.storypublisher.patientdashboard.dto.PatientDetailsDTO;
import com.storypublisher.patientdashboard.model.Patient;
import com.storypublisher.patientdashboard.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @GetMapping
    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        return patientService.getAllPatients(pageable).map(this::toDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        Optional<Patient> patientOpt = patientService.getPatientById(id);
        return patientOpt.map(patient -> ResponseEntity.ok(toDTO(patient)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<PatientDetailsDTO> getPatientDetails(@PathVariable Long id) {
        Optional<PatientDetailsDTO> detailsOpt = patientService.getPatientDetails(id);
        return detailsOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Page<PatientDTO> searchPatients(
            @RequestParam("q") String query,
            @RequestParam("field") String field,
            Pageable pageable
    ) {
        return patientService.searchPatients(query, field, pageable).map(this::toDTO);
    }

    private PatientDTO toDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setFhirId(patient.getFhirId());
        dto.setActive(patient.getActive());
        dto.setGender(patient.getGender());
        dto.setBirthDate(patient.getBirthDate());
        dto.setDeceased(patient.getDeceased());
        dto.setMaritalStatus(patient.getMaritalStatus());
        dto.setMultipleBirth(patient.getMultipleBirth());
        dto.setName(patient.getName());
        return dto;
    }
}
