package com.ibrahim.jobtracker.jobs;

import java.time.LocalDate;

import com.ibrahim.jobtracker.entity.ApplicationStatus;
import com.ibrahim.jobtracker.entity.JobApplication;
import com.ibrahim.jobtracker.entity.Role;
import com.ibrahim.jobtracker.entity.User;
import com.ibrahim.jobtracker.repository.JobApplicationRepository;
import com.ibrahim.jobtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class JobStatsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void shouldReturnStatsForCurrentUserOnly() throws Exception {
        User alice = saveUser("alice", "alice@example.com", Role.USER);
        User bob = saveUser("bob", "bob@example.com", Role.USER);

        saveJob(alice, ApplicationStatus.APPLIED, 1);
        saveJob(alice, ApplicationStatus.INTERVIEW, 2);
        saveJob(alice, ApplicationStatus.INTERVIEW, 3);
        saveJob(alice, ApplicationStatus.OFFER, 4);
        saveJob(alice, ApplicationStatus.REJECTED, 5);
        saveJob(bob, ApplicationStatus.OFFER, 6);

        mockMvc.perform(get("/api/jobs/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").value(5))
                .andExpect(jsonPath("$.interviews").value(2))
                .andExpect(jsonPath("$.offers").value(1))
                .andExpect(jsonPath("$.rejected").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldReturnGlobalStatsForAdmin() throws Exception {
        User alice = saveUser("alice", "alice@example.com", Role.USER);
        User bob = saveUser("bob", "bob@example.com", Role.USER);

        saveJob(alice, ApplicationStatus.APPLIED, 1);
        saveJob(alice, ApplicationStatus.INTERVIEW, 2);
        saveJob(alice, ApplicationStatus.OFFER, 3);
        saveJob(bob, ApplicationStatus.REJECTED, 4);
        saveJob(bob, ApplicationStatus.OFFER, 5);

        mockMvc.perform(get("/api/jobs/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").value(5))
                .andExpect(jsonPath("$.interviews").value(1))
                .andExpect(jsonPath("$.offers").value(2))
                .andExpect(jsonPath("$.rejected").value(1));
    }

    private User saveUser(String username, String email, Role role) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password("hashed")
                .role(role)
                .build();
        return userRepository.save(user);
    }

    private void saveJob(User user, ApplicationStatus status, int dayOffset) {
        JobApplication application = JobApplication.builder()
                .companyName("Company-" + dayOffset)
                .position("Position-" + dayOffset)
                .status(status)
                .appliedDate(LocalDate.now().minusDays(dayOffset))
                .notes("Notes-" + dayOffset)
                .user(user)
                .build();
        jobApplicationRepository.save(application);
    }
}
