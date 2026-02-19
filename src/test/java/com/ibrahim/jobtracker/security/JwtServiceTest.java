package com.ibrahim.jobtracker.security;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "VGhpc0lzQVNlY3VyZVNlY3JldEtleUZvckpXVFRva2VuMTIzNDU2Nzg5MDEyMw=="
        );
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        UserDetails userDetails = new User(
                "alice",
                "hashed",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
        assertThat(jwtService.getExpirationMs()).isEqualTo(3600000L);
    }

    @Test
    void shouldReturnInvalidForDifferentUser() {
        UserDetails sourceUser = new User(
                "alice",
                "hashed",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        UserDetails otherUser = new User(
                "bob",
                "hashed",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(sourceUser);

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }
}
