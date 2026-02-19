package com.ibrahim.jobtracker.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.ibrahim.jobtracker.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobApplicationResponse {
    private Long id;
    private String companyName;
    private String position;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private String notes;
    private Long userId;
    private Instant createdAt;
    private Instant updatedAt;
}
