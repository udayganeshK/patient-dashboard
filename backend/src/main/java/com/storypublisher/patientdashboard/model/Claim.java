package com.storypublisher.patientdashboard.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patient_claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private String claimId;
    private String claimType;
    private LocalDate serviceStart;
    private LocalDate serviceEnd;
    private Double totalCost;
    private String currency;

    @Column(columnDefinition = "TEXT")
    private String rawJson;
    private String claimDescription;
    private String hospital;

    public Double getTotalCost() {
        return totalCost;
    }

    public String getClaimType() {
        return claimType;
    }

    public String getHospital() {
        return hospital;
    }

    public Patient getPatient() {
        return patient;
    }
}
