package com.storypublisher.patientdashboard.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fhirId;

    private Boolean active;
    private String gender;
    private LocalDate birthDate;
    private Boolean deceased;
    private String maritalStatus;
    private Boolean multipleBirth;
    private String name; // Added name field
    private String birthPlace; // Added birthplace field

    // Add more fields as needed

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }
}
