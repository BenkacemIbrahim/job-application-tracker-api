package com.ibrahim.jobtracker.dto;

import com.ibrahim.jobtracker.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthRegisterResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
