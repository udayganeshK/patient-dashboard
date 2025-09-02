package com.storypublisher.patientdashboard.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientDTO {
    private Long id;
    private String fhirId;
    private Boolean active;
    private String gender;
    private LocalDate birthDate;
    private Boolean deceased;
    private String maritalStatus;
    private Boolean multipleBirth;
    private String name;
}
