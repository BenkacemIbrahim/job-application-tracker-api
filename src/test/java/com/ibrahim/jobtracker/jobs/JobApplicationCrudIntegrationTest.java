package com.ibrahim.jobtracker.jobs;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class JobApplicationCrudIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Test
    @WithMockUser(username = "john", roles = {"USER"})
    void userShouldPerformCrudAndListOnlyOwnJobs() throws Exception {
        User john = saveUser("john", "john@example.com", Role.USER);
        User mary = saveUser("mary", "mary@example.com", Role.USER);
        saveJob(mary, ApplicationStatus.APPLIED, "Other Company");

        String requestBody = """
                {
                  "companyName": "OpenAI",
                  "position": "Backend Engineer",
                  "status": "APPLIED",
                  "appliedDate": "2026-02-10",
                  "notes": "Initial submission"
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("OpenAI"))
                .andExpect(jsonPath("$.userId").value(john.getId()))
                .andReturn();

        JsonNode createdJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long createdId = createdJson.get("id").asLong();

        mockMvc.perform(get("/api/jobs")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(createdId));

        String updateBody = """
                {
                  "companyName": "OpenAI Updated",
                  "position": "Senior Backend Engineer",
                  "status": "INTERVIEW",
                  "appliedDate": "2026-02-11",
                  "notes": "Phone screen scheduled"
                }
                """;

        mockMvc.perform(put("/api/jobs/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("OpenAI Updated"))
                .andExpect(jsonPath("$.status").value("INTERVIEW"));

        mockMvc.perform(delete("/api/jobs/{id}", createdId))
                .andExpect(status().isNoContent());

        assertThat(jobApplicationRepository.findById(createdId)).isEmpty();
    }

    @Test
    @WithMockUser(username = "john", roles = {"USER"})
    void userShouldGetForbiddenWhenUpdatingOtherUsersJob() throws Exception {
        saveUser("john", "john@example.com", Role.USER);
        User mary = saveUser("mary", "mary@example.com", Role.USER);
        JobApplication maryJob = saveJob(mary, ApplicationStatus.APPLIED, "Company-X");

        String updateBody = """
                {
                  "companyName": "Unauthorized Update",
                  "position": "Position",
                  "status": "INTERVIEW",
                  "appliedDate": "2026-02-12",
                  "notes": "Attempt"
                }
                """;

        mockMvc.perform(put("/api/jobs/{id}", maryJob.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You can only access your own job applications"));
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

    private JobApplication saveJob(User user, ApplicationStatus status, String companyName) {
        JobApplication application = JobApplication.builder()
                .companyName(companyName)
                .position("Developer")
                .status(status)
                .appliedDate(LocalDate.now().minusDays(1))
                .notes("notes")
                .user(user)
                .build();
        return jobApplicationRepository.save(application);
    }
}
