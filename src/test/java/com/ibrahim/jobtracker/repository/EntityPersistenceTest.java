package com.ibrahim.jobtracker.repository;

import java.time.LocalDate;
import java.util.List;

import com.ibrahim.jobtracker.entity.ApplicationStatus;
import com.ibrahim.jobtracker.entity.JobApplication;
import com.ibrahim.jobtracker.entity.Role;
import com.ibrahim.jobtracker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EntityPersistenceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSaveUserAndJobApplicationUsingCascade() {
        User user = User.builder()
                .username("ibrahim")
                .email("ibrahim@example.com")
                .password("hashed-password")
                .role(Role.USER)
                .build();

        JobApplication application = JobApplication.builder()
                .companyName("OpenAI")
                .position("Backend Engineer")
                .status(ApplicationStatus.APPLIED)
                .appliedDate(LocalDate.now())
                .notes("Initial application submitted")
                .build();

        user.addJobApplication(application);
        User savedUser = userRepository.saveAndFlush(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getJobApplications()).hasSize(1);

        JobApplication savedApplication = savedUser.getJobApplications().get(0);
        assertThat(savedApplication.getId()).isNotNull();
        assertThat(savedApplication.getCreatedAt()).isNotNull();
        assertThat(savedApplication.getUser().getId()).isEqualTo(savedUser.getId());

        List<JobApplication> applicationsByUser = jobApplicationRepository.findByUserId(savedUser.getId());
        assertThat(applicationsByUser).hasSize(1);
        assertThat(applicationsByUser.get(0).getPosition()).isEqualTo("Backend Engineer");
    }

    @Test
    void shouldGenerateExpectedTablesAndColumns() {
        assertThat(tableExists("users")).isTrue();
        assertThat(tableExists("job_applications")).isTrue();

        List<String> userColumns = jdbcTemplate.queryForList(
                "SELECT UPPER(COLUMN_NAME) FROM INFORMATION_SCHEMA.COLUMNS WHERE UPPER(TABLE_NAME) = 'USERS'",
                String.class
        );
        assertThat(userColumns).contains(
                "ID", "USERNAME", "EMAIL", "PASSWORD", "ROLE", "CREATED_AT", "UPDATED_AT"
        );

        List<String> jobApplicationColumns = jdbcTemplate.queryForList(
                "SELECT UPPER(COLUMN_NAME) FROM INFORMATION_SCHEMA.COLUMNS WHERE UPPER(TABLE_NAME) = 'JOB_APPLICATIONS'",
                String.class
        );
        assertThat(jobApplicationColumns).contains(
                "ID", "COMPANY_NAME", "POSITION", "STATUS", "APPLIED_DATE",
                "NOTES", "USER_ID", "CREATED_AT", "UPDATED_AT"
        );
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = UPPER(?)",
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }
}
