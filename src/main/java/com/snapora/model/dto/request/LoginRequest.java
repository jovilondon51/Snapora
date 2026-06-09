package com.snapora.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank private String usernameOrEmail;
    @NotBlank private String password;

    public String getUsernameOrEmail() { return usernameOrEmail; }
    public void setUsernameOrEmail(String u) { this.usernameOrEmail = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
}
