package com.snapora.model.dto.response;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private UserSummary user;

    public AuthResponse() {}

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public UserSummary getUser() { return user; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AuthResponse r = new AuthResponse();
        public Builder accessToken(String v) { r.accessToken = v; return this; }
        public Builder refreshToken(String v) { r.refreshToken = v; return this; }
        public Builder tokenType(String v) { r.tokenType = v; return this; }
        public Builder user(UserSummary v) { r.user = v; return this; }
        public AuthResponse build() { return r; }
    }
}
