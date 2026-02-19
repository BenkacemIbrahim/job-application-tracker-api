package com.ibrahim.jobtracker.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.ibrahim.jobtracker.dto.JobApplicationRequest;
import com.ibrahim.jobtracker.dto.JobApplicationResponse;
import com.ibrahim.jobtracker.entity.ApplicationStatus;
import com.ibrahim.jobtracker.entity.JobApplication;
import com.ibrahim.jobtracker.entity.Role;
import com.ibrahim.jobtracker.entity.User;
import com.ibrahim.jobtracker.exception.ResourceNotFoundException;
import com.ibrahim.jobtracker.repository.JobApplicationRepository;
import com.ibrahim.jobtracker.repository.UserRepository;
import com.ibrahim.jobtracker.util.JobApplicationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceImplTest {

    @Mock
    private JobApplicationRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobApplicationMapper mapper;

    @InjectMocks
    private JobApplicationServiceImpl service;

    @Test
    void createShouldAssignCurrentUserAndSave() {
        JobApplicationRequest request = sampleRequest();
        User user = sampleUser(1L, "john");
        JobApplication entity = new JobApplication();
        JobApplication saved = new JobApplication();
        JobApplicationResponse response = JobApplicationResponse.builder().id(100L).build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        JobApplicationResponse result = service.create(request, "john");

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(entity.getUser()).isEqualTo(user);
        verify(repository).save(entity);
    }

    @Test
    void getJobsShouldReturnOnlyCurrentUserJobsWhenNotAdmin() {
        User user = sampleUser(1L, "john");
        JobApplication application = sampleApplication(10L, user);
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobApplication> page = new PageImpl<>(List.of(application), pageable, 1);
        JobApplicationResponse response = JobApplicationResponse.builder().id(10L).build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(repository.findAllByUserIdAndStatus(1L, ApplicationStatus.APPLIED, pageable)).thenReturn(page);
        when(mapper.toResponse(application)).thenReturn(response);

        Page<JobApplicationResponse> result = service.getJobs(
                ApplicationStatus.APPLIED,
                pageable,
                "john",
                false
        );

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(repository).findAllByUserIdAndStatus(1L, ApplicationStatus.APPLIED, pageable);
    }

    @Test
    void getJobsShouldReturnAllJobsForAdmin() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<JobApplicationResponse> result = service.getJobs(null, pageable, "admin", true);

        assertThat(result.getTotalElements()).isZero();
        verify(repository).findAll(pageable);
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void updateShouldThrowAccessDeniedWhenUserTriesToUpdateOthersJob() {
        User owner = sampleUser(2L, "owner");
        User currentUser = sampleUser(1L, "john");
        JobApplication application = sampleApplication(11L, owner);
        JobApplicationRequest request = sampleRequest();

        when(repository.findById(11L)).thenReturn(Optional.of(application));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(currentUser));

        assertThatThrownBy(() -> service.update(11L, request, "john", false))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("own job applications");
    }

    @Test
    void updateShouldWorkForOwner() {
        User currentUser = sampleUser(1L, "john");
        JobApplication application = sampleApplication(11L, currentUser);
        JobApplicationRequest request = sampleRequest();
        JobApplicationResponse response = JobApplicationResponse.builder().id(11L).build();

        when(repository.findById(11L)).thenReturn(Optional.of(application));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(currentUser));
        when(repository.save(application)).thenReturn(application);
        when(mapper.toResponse(application)).thenReturn(response);

        JobApplicationResponse result = service.update(11L, request, "john", false);

        assertThat(result.getId()).isEqualTo(11L);
        verify(mapper).updateEntity(application, request);
        verify(repository).save(application);
    }

    @Test
    void deleteShouldThrowNotFoundWhenIdDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L, "john", true))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteShouldWorkForAdmin() {
        JobApplication application = sampleApplication(15L, sampleUser(2L, "owner"));
        when(repository.findById(15L)).thenReturn(Optional.of(application));

        service.delete(15L, "admin", true);

        verify(repository).delete(application);
    }

    @Test
    void deleteShouldThrowAccessDeniedWhenUserDeletesOthersJob() {
        User owner = sampleUser(2L, "owner");
        User currentUser = sampleUser(1L, "john");
        JobApplication application = sampleApplication(15L, owner);

        when(repository.findById(15L)).thenReturn(Optional.of(application));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(currentUser));

        assertThatThrownBy(() -> service.delete(15L, "john", false))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("own job applications");
    }

    private User sampleUser(Long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .email(username + "@example.com")
                .password("hashed")
                .role(Role.USER)
                .build();
    }

    private JobApplication sampleApplication(Long id, User user) {
        return JobApplication.builder()
                .id(id)
                .companyName("OpenAI")
                .position("Backend Engineer")
                .status(ApplicationStatus.APPLIED)
                .appliedDate(LocalDate.now())
                .notes("note")
                .user(user)
                .build();
    }

    private JobApplicationRequest sampleRequest() {
        JobApplicationRequest request = new JobApplicationRequest();
        request.setCompanyName("OpenAI");
        request.setPosition("Backend Engineer");
        request.setStatus(ApplicationStatus.APPLIED);
        request.setAppliedDate(LocalDate.now());
        request.setNotes("Initial application");
        return request;
    }
}
