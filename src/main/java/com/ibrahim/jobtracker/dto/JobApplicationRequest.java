package com.ibrahim.jobtracker.dto;

import java.time.LocalDate;

import com.ibrahim.jobtracker.entity.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 150, message = "Company name must not exceed 150 characters")
    private String companyName;

    @NotBlank(message = "Position is required")
    @Size(max = 150, message = "Position must not exceed 150 characters")
    private String position;

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    @NotNull(message = "Applied date is required")
    @PastOrPresent(message = "Applied date cannot be in the future")
    private LocalDate appliedDate;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;
}
