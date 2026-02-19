package com.ibrahim.jobtracker.controller;

import com.ibrahim.jobtracker.dto.JobApplicationRequest;
import com.ibrahim.jobtracker.dto.JobApplicationResponse;
import com.ibrahim.jobtracker.entity.ApplicationStatus;
import com.ibrahim.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER','ADMIN')")
@RequestMapping("/api/jobs")
public class JobApplicationController {

    private static final int MAX_SIZE = 100;

    private final JobApplicationService service;

    @GetMapping
    public ResponseEntity<Page<JobApplicationResponse>> getJobs(
            @RequestParam(name = "status", required = false) ApplicationStatus status,
            @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(MAX_SIZE) int size,
            @RequestParam(name = "sort", defaultValue = "desc") String sort,
            Authentication authentication
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, "appliedDate"));
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");

        return ResponseEntity.ok(service.getJobs(status, pageRequest, authentication.getName(), isAdmin));
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> create(
            @Valid @RequestBody JobApplicationRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationRequest request,
            Authentication authentication
    ) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        return ResponseEntity.ok(service.update(id, request, authentication.getName(), isAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        service.delete(id, authentication.getName(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}
