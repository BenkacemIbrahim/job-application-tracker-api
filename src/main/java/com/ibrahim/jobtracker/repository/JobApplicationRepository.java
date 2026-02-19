package com.ibrahim.jobtracker.repository;

import java.util.List;
import java.util.Optional;

import com.ibrahim.jobtracker.dto.JobApplicationStatsResponse;
import com.ibrahim.jobtracker.entity.ApplicationStatus;
import com.ibrahim.jobtracker.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByStatus(ApplicationStatus status);

    List<JobApplication> findByUserId(Long userId);

    Page<JobApplication> findAllByUserId(Long userId, Pageable pageable);

    Page<JobApplication> findAllByStatus(ApplicationStatus status, Pageable pageable);

    Page<JobApplication> findAllByUserIdAndStatus(Long userId, ApplicationStatus status, Pageable pageable);

    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);

    @Query("""
            SELECT new com.ibrahim.jobtracker.dto.JobApplicationStatsResponse(
                COUNT(j),
                COALESCE(SUM(CASE WHEN j.status = com.ibrahim.jobtracker.entity.ApplicationStatus.INTERVIEW THEN 1 ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN j.status = com.ibrahim.jobtracker.entity.ApplicationStatus.OFFER THEN 1 ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN j.status = com.ibrahim.jobtracker.entity.ApplicationStatus.REJECTED THEN 1 ELSE 0 END), 0)
            )
            FROM JobApplication j
            """)
    JobApplicationStatsResponse getStatsForAll();

    @Query("""
            SELECT new com.ibrahim.jobtracker.dto.JobApplicationStatsResponse(
                COUNT(j),
                COALESCE(SUM(CASE WHEN j.status = com.ibrahim.jobtracker.entity.ApplicationStatus.INTERVIEW THEN 1 ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN j.status = com.ibrahim.jobtracker.entity.ApplicationStatus.OFFER THEN 1 ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN j.status = com.ibrahim.jobtracker.entity.ApplicationStatus.REJECTED THEN 1 ELSE 0 END), 0)
            )
            FROM JobApplication j
            WHERE j.user.id = :userId
            """)
    JobApplicationStatsResponse getStatsByUserId(@Param("userId") Long userId);
}
