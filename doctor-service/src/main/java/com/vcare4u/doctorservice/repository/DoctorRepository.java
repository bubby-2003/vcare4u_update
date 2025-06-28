package com.vcare4u.doctorservice.repository;

import com.vcare4u.doctorservice.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

//DoctorRepository.java
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
 Optional<Doctor> findByEmail(String email);  // ✅ Add this
}

