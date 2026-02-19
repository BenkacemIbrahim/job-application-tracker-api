package com.ibrahim.jobtracker.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthTokenResponse {
    private String token;
    private String tokenType;
    private long expiresIn;
}
