package com.vcare4u.appointmentservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.vcare4u.appointmentservice.dto.AppointmentDto;
import com.vcare4u.appointmentservice.dto.DoctorDto;
import com.vcare4u.appointmentservice.dto.PatientDto;
import com.vcare4u.appointmentservice.exception.ResourceNotFoundException;
import com.vcare4u.appointmentservice.feign.DoctorClient;
import com.vcare4u.appointmentservice.feign.PatientClient;
import com.vcare4u.appointmentservice.model.Appointment;
import com.vcare4u.appointmentservice.repository.AppointmentRepository;
import com.vcare4u.appointmentservice.service.AppointmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorClient doctorClient;
    private final PatientClient patientClient;
 
    @Override
    public List<AppointmentDto> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto updateAppointmentStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        appointment.setStatus(status);
        return mapToDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDto createAppointment(AppointmentDto dto) {
        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(dto, appointment);
        return mapToDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDto getAppointmentById(Long id, String token) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        AppointmentDto dto = mapToDto(appointment);

        try {
            DoctorDto doctor = doctorClient.getDoctorById(dto.getDoctorId(), token);
            PatientDto patient = patientClient.getPatientById(dto.getPatientId(), token);
           // dto.setNotes("Doctor: " + doctor.getFullName() + " with  Patient: " + patient.getFullName() + " Confirmed ");
        } catch (Exception e) {
            //dto.setNotes("Doctor or Patient details not available");
        }

        return dto;
    }
    



    @Override
    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDto updateAppointment(Long id, AppointmentDto dto) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        existing.setPatientId(dto.getPatientId());
        existing.setDoctorId(dto.getDoctorId());
        existing.setReason(dto.getReason());
        existing.setAppointmentDateTime(dto.getAppointmentDateTime());
        existing.setStatus(dto.getStatus());
//        existing.setNotes(dto.getNotes());

        return mapToDto(appointmentRepository.save(existing));
    }

    @Override
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        appointmentRepository.delete(appointment);
    }

    private AppointmentDto mapToDto(Appointment appointment) {
        AppointmentDto dto = new AppointmentDto();
        BeanUtils.copyProperties(appointment, dto);
        return dto;
    }
    @Override
    public List<AppointmentDto> getAppointmentsByPatientEmail(String email, String token) {
        // Get PatientDto using Feign
        PatientDto patient = patientClient.getPatientByEmail(email, token);
        
        if (patient == null || patient.getId() == null) {
            throw new ResourceNotFoundException("Patient", "email", email);
        }

        return appointmentRepository.findByPatientId(patient.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

}
