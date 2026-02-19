package com.ibrahim.jobtracker.dto;

import com.ibrahim.jobtracker.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;
}
