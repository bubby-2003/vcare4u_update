package com.vcare4u.authservice;

import com.vcare4u.authservice.exception.ResourceNotFoundException;
import com.vcare4u.authservice.model.Role;
import com.vcare4u.authservice.model.User;
import com.vcare4u.authservice.repository.UserRepository;
import com.vcare4u.authservice.service.CustomUserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = User.builder()
                .email("testuser@example.com")
                .password("testpass")
                .role(Role.PATIENT)
                .build();
    }

    @Test
    void testLoadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser@example.com");

        assertNotNull(userDetails);
        assertEquals("testuser@example.com", userDetails.getUsername());
        assertEquals("testpass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_PATIENT")));
    }

    @Test
    void testLoadUserByUsername_WhenUserNotFound_ShouldThrowResourceNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("unknown@example.com");
        });

//        assertEquals("User not found with email : 'unknown@example.com'", exception.getMessage());
    }

}
