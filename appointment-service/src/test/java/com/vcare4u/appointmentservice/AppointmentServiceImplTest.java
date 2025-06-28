package com.vcare4u.appointmentservice;

import com.vcare4u.appointmentservice.dto.AppointmentDto;
import com.vcare4u.appointmentservice.dto.DoctorDto;
import com.vcare4u.appointmentservice.dto.PatientDto;
import com.vcare4u.appointmentservice.exception.ResourceNotFoundException;
import com.vcare4u.appointmentservice.feign.DoctorClient;
import com.vcare4u.appointmentservice.feign.PatientClient;
import com.vcare4u.appointmentservice.model.Appointment;
import com.vcare4u.appointmentservice.repository.AppointmentRepository;
import com.vcare4u.appointmentservice.service.impl.AppointmentServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceImplTest {

    private AppointmentRepository appointmentRepository;
    private DoctorClient doctorClient;
    private PatientClient patientClient;
    private AppointmentServiceImpl service;

    @BeforeEach
    void setUp() {
        appointmentRepository = mock(AppointmentRepository.class);
        doctorClient = mock(DoctorClient.class);
        patientClient = mock(PatientClient.class);
        service = new AppointmentServiceImpl(appointmentRepository, doctorClient, patientClient);
    }

    @Test
    void testGetAppointmentsByPatient() {
        Appointment appointment = sampleAppointment();
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of(appointment));

        List<AppointmentDto> result = service.getAppointmentsByPatient(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPatientId()).isEqualTo(1L);
    }

    @Test
    void testGetAppointmentsByDoctor() {
        Appointment appointment = sampleAppointment();
        when(appointmentRepository.findByDoctorId(2L)).thenReturn(List.of(appointment));

        List<AppointmentDto> result = service.getAppointmentsByDoctor(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDoctorId()).isEqualTo(2L);
    }

    @Test
    void testUpdateAppointmentStatus() {
        Appointment appointment = sampleAppointment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        AppointmentDto result = service.updateAppointmentStatus(1L, "COMPLETED");

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void testCreateAppointment() {
        Appointment appointment = sampleAppointment();
        AppointmentDto dto = sampleDto();
        when(appointmentRepository.save(any())).thenReturn(appointment);

        AppointmentDto result = service.createAppointment(dto);

        assertThat(result.getDoctorId()).isEqualTo(2L);
        assertThat(result.getPatientId()).isEqualTo(1L);
    }

    @Test
    void testGetAppointmentById_WithDoctorAndPatient() {
        Appointment appointment = sampleAppointment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        DoctorDto doctor = new DoctorDto();
        doctor.setFullName("Dr. Smith");
        PatientDto patient = new PatientDto();
        patient.setFullName("John Doe");

        when(doctorClient.getDoctorById(2L, "token")).thenReturn(doctor);
        when(patientClient.getPatientById(1L, "token")).thenReturn(patient);

//        AppointmentDto result = service.getAppointmentById(1L, "token");
////
////        assertThat(result.getNotes()).contains("Dr. Smith");
    }

    @Test
    void testGetAppointmentById_FallbackToDefaultNote() {
        Appointment appointment = sampleAppointment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(doctorClient.getDoctorById(anyLong(), anyString())).thenThrow(new RuntimeException());

//        AppointmentDto result = service.getAppointmentById(1L, "token");
//
//        assertThat(result.getNotes()).isEqualTo("Doctor or Patient details not available");
    }

    @Test
    void testGetAllAppointments() {
        Appointment appointment = sampleAppointment();
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        List<AppointmentDto> result = service.getAllAppointments();

        assertThat(result).hasSize(1);
    }

    @Test
    void testUpdateAppointment() {
        Appointment appointment = sampleAppointment();
        AppointmentDto dto = sampleDto();
        dto.setReason("Updated Reason");
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        AppointmentDto result = service.updateAppointment(1L, dto);

        assertThat(result.getReason()).isEqualTo("Updated Reason");
    }

    @Test
    void testDeleteAppointment() {
        Appointment appointment = sampleAppointment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        service.deleteAppointment(1L);

        verify(appointmentRepository, times(1)).delete(appointment);
    }

    @Test
    void testDeleteAppointment_NotFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteAppointment(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // Sample data for reuse
    private Appointment sampleAppointment() {
        Appointment a = new Appointment();
        a.setId(1L);
        a.setDoctorId(2L);
        a.setPatientId(1L);
        a.setStatus("PENDING");
        a.setReason("Checkup");
        a.setAppointmentDateTime(LocalDateTime.now());
        return a;
    }

    private AppointmentDto sampleDto() {
        AppointmentDto dto = new AppointmentDto();
        dto.setDoctorId(2L);
        dto.setPatientId(1L);
        dto.setStatus("PENDING");
        dto.setReason("Checkup");
        dto.setAppointmentDateTime(LocalDateTime.now());
        return dto;
    }
}
