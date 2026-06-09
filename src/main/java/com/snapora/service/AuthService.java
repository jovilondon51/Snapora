package com.snapora.service;

import com.snapora.model.dto.request.LoginRequest;
import com.snapora.model.dto.request.RefreshTokenRequest;
import com.snapora.model.dto.request.RegisterRequest;
import com.snapora.model.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(RefreshTokenRequest request);
}
