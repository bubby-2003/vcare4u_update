package com.vcare4u.doctorservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                // 🔐 Only ADMIN or DOCTOR can CREATE
                .requestMatchers(HttpMethod.POST, "/api/doctors").hasAnyRole("ADMIN", "DOCTOR")

                // 🔐 Only ADMIN can DELETE
                .requestMatchers(HttpMethod.DELETE, "/api/doctors/**").hasRole("ADMIN")

                // ✅ Allow doctors to fetch themselves by email
                .requestMatchers(HttpMethod.GET, "/api/doctors/email/**").hasAnyRole("DOCTOR", "ADMIN")

                // ✅ Admin, Doctor, and Patient can fetch by ID
                .requestMatchers(HttpMethod.GET, "/api/doctors/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")

                // ✅ Admin & Doctor can update
                .requestMatchers(HttpMethod.PUT, "/api/doctors/**").hasAnyRole("ADMIN", "DOCTOR")

                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
