package com.ibrahim.jobtracker.repository;

import java.util.List;
import java.util.Optional;

import com.ibrahim.jobtracker.entity.ApplicationStatus;
import com.ibrahim.jobtracker.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByStatus(ApplicationStatus status);

    List<JobApplication> findByUserId(Long userId);

    Page<JobApplication> findAllByUserId(Long userId, Pageable pageable);

    Page<JobApplication> findAllByStatus(ApplicationStatus status, Pageable pageable);

    Page<JobApplication> findAllByUserIdAndStatus(Long userId, ApplicationStatus status, Pageable pageable);

    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
}
