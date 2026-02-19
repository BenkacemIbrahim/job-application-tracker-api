package com.ibrahim.jobtracker.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibrahim.jobtracker.dto.AuthLoginRequest;
import com.ibrahim.jobtracker.dto.AuthRegisterRequest;
import com.ibrahim.jobtracker.entity.Role;
import com.ibrahim.jobtracker.entity.User;
import com.ibrahim.jobtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        AuthRegisterRequest request = new AuthRegisterRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("StrongPass123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        User saved = userRepository.findByUsername("newuser").orElseThrow();
        assertThat(saved.getPassword()).isNotEqualTo("StrongPass123");
        assertThat(passwordEncoder.matches("StrongPass123", saved.getPassword())).isTrue();
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        User user = User.builder()
                .username("loginuser")
                .email("loginuser@example.com")
                .password(passwordEncoder.encode("CorrectPassword123"))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        AuthLoginRequest request = new AuthLoginRequest();
        request.setUsername("loginuser");
        request.setPassword("WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username/email or password"));
    }

    @Test
    void shouldDenyAccessToProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isUnauthorized());
    }
}
