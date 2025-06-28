package com.vcare4u.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class PatientDto {
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;
    @Email
    private String email;
    private String phone;
    private String gender;
    private String address;
    private String medicalHistory;
}