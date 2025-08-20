package shujaa.authentication_with_spring.security.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shujaa.authentication_with_spring.security.domain.availability.AvailabilityBlock;
import shujaa.authentication_with_spring.security.domain.availability.AvailabilityBlockRepository;
import shujaa.authentication_with_spring.security.domain.doctor.Doctor;
import shujaa.authentication_with_spring.security.service.availability.AvailabilityService;
import shujaa.authentication_with_spring.security.service.doctor.DoctorService;
import shujaa.authentication_with_spring.security.web.dto.AvailabilityBlockRequest;
import shujaa.authentication_with_spring.security.web.dto.AvailabilitySlot;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final DoctorService doctorService;
    private final AvailabilityBlockRepository blockRepository;

    /** Botpress will call this without authentication; doctorId is required */
    @GetMapping
    public ResponseEntity<List<AvailabilitySlot>> getSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) throws Exception {
        Doctor doctor = doctorService.getByIdOrThrow(doctorId);
        return ResponseEntity.ok(availabilityService.computeAvailableSlots(doctor, from, to));
    }

    /** Doctor blocks a time range */
    @PostMapping("/block")
    public ResponseEntity<AvailabilityBlock> block(@Valid @RequestBody AvailabilityBlockRequest req) {
        Doctor doctor = doctorService.getByIdOrThrow(req.getDoctorId());
        AvailabilityBlock b = new AvailabilityBlock();
        b.setDoctor(doctor);
        b.setStart(req.getStart());
        b.setEnd(req.getEnd());
        b.setReason(req.getReason());
        return ResponseEntity.ok(blockRepository.save(b));
    }
}
