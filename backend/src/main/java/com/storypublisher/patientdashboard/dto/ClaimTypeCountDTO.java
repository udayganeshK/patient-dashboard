package com.storypublisher.patientdashboard.dto;

public class ClaimTypeCountDTO {
    private String claimType;
    private long count;

    public ClaimTypeCountDTO(String claimType, long count) {
        this.claimType = claimType;
        this.count = count;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
