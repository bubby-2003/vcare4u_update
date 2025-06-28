package com.vcare4u.doctorservice.controller;

import com.vcare4u.doctorservice.dto.DoctorDto;
import com.vcare4u.doctorservice.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor(@RequestBody DoctorDto dto) {
        return ResponseEntity.ok(doctorService.createDoctor(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable Long id, @RequestBody DoctorDto dto) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<DoctorDto> getDoctorByEmail(@PathVariable String email) {
        return ResponseEntity.ok(doctorService.getDoctorByEmail(email));
    }

}
