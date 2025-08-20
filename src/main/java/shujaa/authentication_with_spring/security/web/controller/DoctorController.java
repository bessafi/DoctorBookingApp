package shujaa.authentication_with_spring.security.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shujaa.authentication_with_spring.security.domain.doctor.Doctor;
import shujaa.authentication_with_spring.security.service.doctor.DoctorService;
import shujaa.authentication_with_spring.security.web.dto.DoctorProfileDto;

import java.net.URI;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/me")
    public ResponseEntity<Doctor> me() {
        Doctor d = doctorService.getOrCreateForCurrentUser();
        return ResponseEntity.ok(d);
    }

    @PutMapping("/me")
    public ResponseEntity<Doctor> update(@Valid @RequestBody DoctorProfileDto dto) {
        Doctor d = doctorService.getOrCreateForCurrentUser();
        d.setFirstName(dto.getFirstName());
        d.setLastName(dto.getLastName());
        d.setPhone(dto.getPhone());
        d.setSpecialty(dto.getSpecialty());
        d.setTimezone(dto.getTimezone());
        d.setCalendarId(dto.getCalendarId());
        d.setWorkStart(dto.getWorkStart());
        d.setWorkEnd(dto.getWorkEnd());
        d.setSlotMinutes(dto.getSlotMinutes());
        return ResponseEntity.ok(d);
    }
}