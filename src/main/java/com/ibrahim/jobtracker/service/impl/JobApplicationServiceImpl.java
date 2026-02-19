package com.ibrahim.jobtracker.service.impl;

import com.ibrahim.jobtracker.dto.JobApplicationRequest;
import com.ibrahim.jobtracker.dto.JobApplicationResponse;
import com.ibrahim.jobtracker.dto.JobApplicationStatsResponse;
import com.ibrahim.jobtracker.entity.ApplicationStatus;
import com.ibrahim.jobtracker.entity.JobApplication;
import com.ibrahim.jobtracker.entity.User;
import com.ibrahim.jobtracker.exception.ResourceNotFoundException;
import com.ibrahim.jobtracker.repository.JobApplicationRepository;
import com.ibrahim.jobtracker.repository.UserRepository;
import com.ibrahim.jobtracker.service.JobApplicationService;
import com.ibrahim.jobtracker.util.JobApplicationMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository repository;
    private final UserRepository userRepository;
    private final JobApplicationMapper mapper;

    @Override
    public JobApplicationResponse create(JobApplicationRequest request, String username) {
        User currentUser = findCurrentUser(username);
        JobApplication entity = mapper.toEntity(request);
        entity.setUser(currentUser);
        JobApplication saved = repository.save(entity);
        log.info("Created job application id={} for user={}", saved.getId(), username);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getJobs(
            ApplicationStatus status,
            Pageable pageable,
            String username,
            boolean isAdmin
    ) {
        if (isAdmin) {
            Page<JobApplication> adminPage = status == null
                    ? repository.findAll(pageable)
                    : repository.findAllByStatus(status, pageable);
            log.debug("Fetched jobs for admin with status={} page={} size={}", status, pageable.getPageNumber(), pageable.getPageSize());
            return adminPage.map(mapper::toResponse);
        }

        User currentUser = findCurrentUser(username);
        Page<JobApplication> userPage = status == null
                ? repository.findAllByUserId(currentUser.getId(), pageable)
                : repository.findAllByUserIdAndStatus(currentUser.getId(), status, pageable);
        log.debug("Fetched jobs for user={} with status={} page={} size={}", username, status, pageable.getPageNumber(), pageable.getPageSize());

        return userPage.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplicationStatsResponse getStats(String username, boolean isAdmin) {
        if (isAdmin) {
            JobApplicationStatsResponse stats = repository.getStatsForAll();
            log.info("Computed global job stats for admin={}", username);
            return stats;
        }

        User currentUser = findCurrentUser(username);
        JobApplicationStatsResponse stats = repository.getStatsByUserId(currentUser.getId());
        log.info("Computed job stats for user={}", username);
        return stats;
    }

    @Override
    public JobApplicationResponse update(Long id, JobApplicationRequest request, String username, boolean isAdmin) {
        JobApplication existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));

        User currentUser = isAdmin ? null : findCurrentUser(username);
        enforceOwnership(existing, currentUser, isAdmin);

        mapper.updateEntity(existing, request);
        JobApplication saved = repository.save(existing);
        log.info("Updated job application id={} by user={} admin={}", id, username, isAdmin);
        return mapper.toResponse(saved);
    }

    @Override
    public void delete(Long id, String username, boolean isAdmin) {
        JobApplication existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));

        User currentUser = isAdmin ? null : findCurrentUser(username);
        enforceOwnership(existing, currentUser, isAdmin);

        repository.delete(existing);
        log.info("Deleted job application id={} by user={} admin={}", id, username, isAdmin);
    }

    private User findCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    private void enforceOwnership(JobApplication application, User currentUser, boolean isAdmin) {
        if (isAdmin) {
            return;
        }

        if (application.getUser() == null || !application.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only access your own job applications");
        }
    }
}
