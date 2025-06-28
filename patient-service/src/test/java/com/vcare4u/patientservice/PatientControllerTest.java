package com.vcare4u.patientservice;

import com.vcare4u.patientservice.config.JwtUtils;
import com.vcare4u.patientservice.controller.PatientController;
import com.vcare4u.patientservice.dto.PatientDto;
import com.vcare4u.patientservice.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PatientController patientController;

    private PatientDto sampleDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleDto = PatientDto.builder()
        	    .id(1L)
        	    .fullName("John Doe")
        	    .email("john@example.com")
        	    .phone("9876543210")
        	    .gender("Male")
        	    .address("Bangalore")
        	    .medicalHistory("Diabetes")
        	    .build();

    }

    @Test
    void testCreatePatient() {
        when(patientService.createPatient(sampleDto)).thenReturn(sampleDto);

        ResponseEntity<PatientDto> response = patientController.createPatient(sampleDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John Doe", response.getBody().getFullName());
        verify(patientService, times(1)).createPatient(sampleDto);
    }

    @Test
    void testGetPatientById_WithAdminRole() {
        when(jwtUtils.extractUsernameFromRequest(request)).thenReturn("admin");
        when(jwtUtils.extractRoleFromRequest(request)).thenReturn("ADMIN");
        when(patientService.getPatientById(1L)).thenReturn(sampleDto);

        ResponseEntity<PatientDto> response = patientController.getPatient(1L, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John Doe", response.getBody().getFullName());
    }

    @Test
    void testGetPatientById_ForbiddenRole() {
        when(jwtUtils.extractUsernameFromRequest(request)).thenReturn("user");
        when(jwtUtils.extractRoleFromRequest(request)).thenReturn("NURSE");

        ResponseEntity<PatientDto> response = patientController.getPatient(1L, request);

        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testGetAllPatients_AdminRole() {
        when(jwtUtils.extractRoleFromRequest(request)).thenReturn("ADMIN");
        when(patientService.getAllPatients()).thenReturn(Arrays.asList(sampleDto));

        ResponseEntity<List<PatientDto>> response = patientController.getAllPatients(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetAllPatients_NonAdminRole() {
        when(jwtUtils.extractRoleFromRequest(request)).thenReturn("PATIENT");

        ResponseEntity<List<PatientDto>> response = patientController.getAllPatients(request);

        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testUpdatePatient_AdminRole() {
        when(jwtUtils.extractRoleFromRequest(request)).thenReturn("ADMIN");
        when(patientService.updatePatient(1L, sampleDto)).thenReturn(sampleDto);

        ResponseEntity<PatientDto> response = patientController.updatePatient(1L, sampleDto, request);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testUpdatePatient_ForbiddenRole() {
        when(jwtUtils.extractRoleFromRequest(request)).thenReturn("PATIENT");

        ResponseEntity<PatientDto> response = patientController.updatePatient(1L, sampleDto, request);

        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testDeletePatient() {
        doNothing().when(patientService).deletePatient(1L);

        ResponseEntity<Void> response = patientController.deletePatient(1L);

        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testGetLoggedInPatient() {
        when(jwtUtils.extractUsername("BearerToken")).thenReturn("john@example.com");
        when(patientService.getByEmail("john@example.com")).thenReturn(sampleDto);

        ResponseEntity<PatientDto> response = patientController.getLoggedInPatient("Bearer BearerToken");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John Doe", response.getBody().getFullName());
    }

    @Test
    void testUpdateLoggedInPatient() {
        when(jwtUtils.extractUsername("BearerToken")).thenReturn("john@example.com");
        when(patientService.updateByEmail("john@example.com", sampleDto)).thenReturn(sampleDto);

        ResponseEntity<PatientDto> response = patientController.updateLoggedInPatient("Bearer BearerToken", sampleDto);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetPatientByEmail_WithAuthorizedRole() {
        when(jwtUtils.extractRoleFromRequest(request)).thenReturn("DOCTOR");
        when(patientService.getByEmail("john@example.com")).thenReturn(sampleDto);

        ResponseEntity<PatientDto> response = patientController.getPatientByEmail("john@example.com", request);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetPatientByEmail_WithUnauthorizedRole() {
        when(jwtUtils.extractRoleFromRequest(request)).thenReturn("NURSE");

        ResponseEntity<PatientDto> response = patientController.getPatientByEmail("john@example.com", request);

        assertEquals(403, response.getStatusCodeValue());
    }
}
