package com.storypublisher.patientdashboard.dto;

import java.util.List;

public class PatientDetailsDTO {
    private Long id;
    private String fhirId;
    private String name;
    private String gender;
    private String birthDate;
    private Boolean active;
    private Boolean deceased;
    private List<ClaimDTO> claims;
    private Double totalCost;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFhirId() { return fhirId; }
    public void setFhirId(String fhirId) { this.fhirId = fhirId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Boolean getDeceased() { return deceased; }
    public void setDeceased(Boolean deceased) { this.deceased = deceased; }
    public List<ClaimDTO> getClaims() { return claims; }
    public void setClaims(List<ClaimDTO> claims) { this.claims = claims; }
    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
}
