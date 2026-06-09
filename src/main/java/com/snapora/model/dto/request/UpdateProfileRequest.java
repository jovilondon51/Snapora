package com.snapora.model.dto.request;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    @Size(max = 100) private String fullName;
    @Size(max = 150) private String bio;
    private Boolean isPrivate;

    public String getFullName() { return fullName; }
    public void setFullName(String f) { this.fullName = f; }
    public String getBio() { return bio; }
    public void setBio(String b) { this.bio = b; }
    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean p) { this.isPrivate = p; }
}
