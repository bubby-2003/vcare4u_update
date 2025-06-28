package com.vcare4u.authservice;

import com.vcare4u.authservice.config.JwtUtils;
import com.vcare4u.authservice.dto.AuthRequest;
import com.vcare4u.authservice.dto.AuthResponse;
import com.vcare4u.authservice.dto.RegisterRequest;
import com.vcare4u.authservice.model.Role;
import com.vcare4u.authservice.model.User;
import com.vcare4u.authservice.repository.UserRepository;
import com.vcare4u.authservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        RegisterRequest request = RegisterRequest.builder()
                .username("greeshma")
                .email("greeshma@example.com")
                .password("pass123")
                .role(Role.PATIENT)
                .build();

        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("User registered successfully as PATIENT", response.getMessage());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLogin() {
        AuthRequest request = AuthRequest.builder()
                .email("greeshma@example.com")
                .password("pass123")
                .build();

        User user = User.builder()
                .email("greeshma@example.com")
                .password("encodedPass")
                .role(Role.PATIENT)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("greeshma@example.com", "pass123"));

        when(userRepository.findByEmail("greeshma@example.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken("greeshma@example.com", "PATIENT")).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void testLoginThrowsExceptionForInvalidUser() {
        AuthRequest request = AuthRequest.builder()
                .email("unknown@example.com")
                .password("pass")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("unknown@example.com", "pass"));

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

//        assertEquals("User not found", exception.getMessage());
    }
}
