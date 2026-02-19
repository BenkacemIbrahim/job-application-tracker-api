package com.ibrahim.jobtracker.util;

import java.time.Instant;
import java.time.LocalDate;

import com.ibrahim.jobtracker.dto.JobApplicationRequest;
import com.ibrahim.jobtracker.dto.JobApplicationResponse;
import com.ibrahim.jobtracker.entity.ApplicationStatus;
import com.ibrahim.jobtracker.entity.JobApplication;
import com.ibrahim.jobtracker.entity.Role;
import com.ibrahim.jobtracker.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JobApplicationMapperTest {

    private final JobApplicationMapper mapper = new JobApplicationMapper();

    @Test
    void shouldMapRequestToEntityAndUpdateFields() {
        JobApplicationRequest request = new JobApplicationRequest();
        request.setCompanyName("OpenAI");
        request.setPosition("Backend Engineer");
        request.setStatus(ApplicationStatus.APPLIED);
        request.setAppliedDate(LocalDate.of(2026, 2, 1));
        request.setNotes("initial");

        JobApplication entity = mapper.toEntity(request);

        assertThat(entity.getCompanyName()).isEqualTo("OpenAI");
        assertThat(entity.getPosition()).isEqualTo("Backend Engineer");
        assertThat(entity.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
        assertThat(entity.getAppliedDate()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(entity.getNotes()).isEqualTo("initial");

        request.setCompanyName("Updated");
        request.setPosition("Senior Backend Engineer");
        request.setStatus(ApplicationStatus.INTERVIEW);
        mapper.updateEntity(entity, request);

        assertThat(entity.getCompanyName()).isEqualTo("Updated");
        assertThat(entity.getPosition()).isEqualTo("Senior Backend Engineer");
        assertThat(entity.getStatus()).isEqualTo(ApplicationStatus.INTERVIEW);
    }

    @Test
    void shouldMapEntityToResponse() {
        User user = User.builder()
                .id(5L)
                .username("alice")
                .email("alice@example.com")
                .password("hashed")
                .role(Role.USER)
                .build();

        JobApplication entity = JobApplication.builder()
                .id(11L)
                .companyName("Company")
                .position("Position")
                .status(ApplicationStatus.OFFER)
                .appliedDate(LocalDate.of(2026, 2, 2))
                .notes("notes")
                .user(user)
                .build();
        entity.setCreatedAt(Instant.parse("2026-02-19T10:00:00Z"));
        entity.setUpdatedAt(Instant.parse("2026-02-19T11:00:00Z"));

        JobApplicationResponse response = mapper.toResponse(entity);

        assertThat(response.getId()).isEqualTo(11L);
        assertThat(response.getCompanyName()).isEqualTo("Company");
        assertThat(response.getPosition()).isEqualTo("Position");
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.OFFER);
        assertThat(response.getUserId()).isEqualTo(5L);
        assertThat(response.getCreatedAt()).isEqualTo(Instant.parse("2026-02-19T10:00:00Z"));
        assertThat(response.getUpdatedAt()).isEqualTo(Instant.parse("2026-02-19T11:00:00Z"));
    }
}
