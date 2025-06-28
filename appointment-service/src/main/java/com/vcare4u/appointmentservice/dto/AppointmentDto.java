package com.vcare4u.appointmentservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String reason;
    private LocalDateTime appointmentDateTime;
    private String status;
//    private String notes;

    
}

