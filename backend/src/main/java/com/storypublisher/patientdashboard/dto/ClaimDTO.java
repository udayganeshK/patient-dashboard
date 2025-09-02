package com.storypublisher.patientdashboard.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClaimDTO {
    private Long id;
    private String claimId;
    private String claimType;
    private LocalDate serviceStart;
    private LocalDate serviceEnd;
    private Double totalCost;
    private String currency;
    private Long patientId;
    private String claimDescription;
    private String hospital;
}
