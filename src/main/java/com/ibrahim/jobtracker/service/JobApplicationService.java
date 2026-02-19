package com.ibrahim.jobtracker.service;

import com.ibrahim.jobtracker.dto.JobApplicationRequest;
import com.ibrahim.jobtracker.dto.JobApplicationResponse;
import com.ibrahim.jobtracker.dto.JobApplicationStatsResponse;
import com.ibrahim.jobtracker.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobApplicationService {

    JobApplicationResponse create(JobApplicationRequest request, String username);

    Page<JobApplicationResponse> getJobs(
            ApplicationStatus status,
            Pageable pageable,
            String username,
            boolean isAdmin
    );

    JobApplicationStatsResponse getStats(String username, boolean isAdmin);

    JobApplicationResponse update(Long id, JobApplicationRequest request, String username, boolean isAdmin);

    void delete(Long id, String username, boolean isAdmin);
}
