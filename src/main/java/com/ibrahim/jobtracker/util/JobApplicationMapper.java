package com.ibrahim.jobtracker.util;

import com.ibrahim.jobtracker.dto.JobApplicationRequest;
import com.ibrahim.jobtracker.dto.JobApplicationResponse;
import com.ibrahim.jobtracker.entity.JobApplication;
import org.springframework.stereotype.Component;

@Component
public class JobApplicationMapper {

    public JobApplication toEntity(JobApplicationRequest request) {
        JobApplication entity = new JobApplication();
        updateEntity(entity, request);
        return entity;
    }

    public void updateEntity(JobApplication entity, JobApplicationRequest request) {
        entity.setCompanyName(request.getCompanyName());
        entity.setPosition(request.getPosition());
        entity.setStatus(request.getStatus());
        entity.setAppliedDate(request.getAppliedDate());
        entity.setNotes(request.getNotes());
    }

    public JobApplicationResponse toResponse(JobApplication entity) {
        return JobApplicationResponse.builder()
                .id(entity.getId())
                .companyName(entity.getCompanyName())
                .position(entity.getPosition())
                .status(entity.getStatus())
                .appliedDate(entity.getAppliedDate())
                .notes(entity.getNotes())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
