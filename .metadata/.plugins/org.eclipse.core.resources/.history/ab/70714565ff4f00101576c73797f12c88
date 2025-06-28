package com.vcare4u.patientservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vcare4u.patientservice.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
	Optional<Patient> findByEmail(String email);

}

