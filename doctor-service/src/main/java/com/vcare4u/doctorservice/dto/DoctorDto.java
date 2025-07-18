package com.vcare4u.doctorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DoctorDto {
    private Long id;
    private String fullName;
    private String email;
    private String specialization;
    private String phone;
    private String qualification;
    private String department;
}
