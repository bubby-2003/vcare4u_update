package com.vcare4u.appointmentservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // ✅ Allow DOCTOR or ADMIN to update appointment status
                .requestMatchers(HttpMethod.PUT, "/api/appointments/{id}/status").hasAnyRole("DOCTOR", "ADMIN")


                // ✅ Allow PATIENT role to access appointments by email
                .requestMatchers(HttpMethod.GET, "/api/appointments/patient/**").hasRole("PATIENT")

                // ✅ Allow PATIENT to fetch their own appointments (by ID)
                .requestMatchers(HttpMethod.GET, "/api/appointments/my").hasRole("PATIENT")

                // ✅ Allow DOCTOR to view their appointments
                .requestMatchers(HttpMethod.GET, "/api/appointments/doctor/**").hasRole("DOCTOR")

                // ✅ Allow ADMIN to manage appointments
                .requestMatchers(HttpMethod.GET, "/api/appointments").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/appointments").hasAnyRole("ADMIN", "PATIENT")
                .requestMatchers(HttpMethod.PUT, "/api/appointments/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/appointments/**").hasRole("ADMIN")

              
                // ✅ Allow all roles to fetch appointment by ID
                .requestMatchers(HttpMethod.GET, "/api/appointments/{id}").hasAnyRole("ADMIN", "PATIENT", "DOCTOR")

                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
