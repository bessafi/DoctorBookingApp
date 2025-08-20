package shujaa.authentication_with_spring.security.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shujaa.authentication_with_spring.security.domain.booking.Booking;
import shujaa.authentication_with_spring.security.domain.doctor.Doctor;
import shujaa.authentication_with_spring.security.service.booking.BookingService;
import shujaa.authentication_with_spring.security.service.doctor.DoctorService;
import shujaa.authentication_with_spring.security.web.dto.BookingRequest;
import shujaa.authentication_with_spring.security.web.dto.BookingUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final DoctorService doctorService;

    /** Botpress creates a booking (no auth) */
    @PostMapping
    public ResponseEntity<Booking> create(@Valid @RequestBody BookingRequest req) throws Exception {
        Doctor doctor = doctorService.getByIdOrThrow(req.getDoctorId());
        Booking draft = new Booking();
        draft.setDoctor(doctor);
        draft.setPatientName(req.getPatientName());
        draft.setPatientEmail(req.getPatientEmail());
        draft.setReason(req.getReason());
        draft.setStart(req.getStart());
        draft.setEnd(req.getEnd());
        return ResponseEntity.ok(bookingService.create(doctor, draft));
    }

    /** Doctor lists his bookings (auth required) */
    @GetMapping
    public ResponseEntity<List<Booking>> list(@RequestParam Long doctorId) {
        return ResponseEntity.ok(bookingService.listByDoctor(doctorId));
    }

    /** Doctor updates a booking */
    @PutMapping("/{id}")
    public ResponseEntity<Booking> update(@PathVariable Long id, @Valid @RequestBody BookingUpdateRequest req) throws Exception {
        // We derive doctor via booking’s doctor to keep it simple:
        // In real scenario you’d check the current user owns the doctor.
        Booking b = bookingService.listByDoctor(null).stream().filter(x -> id.equals(x.getId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        Doctor d = b.getDoctor();
        return ResponseEntity.ok(bookingService.update(d, id, req.getStart(), req.getEnd(), req.getReason()));
    }

    /** Doctor cancels a booking */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) throws Exception {
        // Same note about ownership
        Booking b = bookingService.listByDoctor(null).stream().filter(x -> id.equals(x.getId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        bookingService.cancel(b.getDoctor(), id);
        return ResponseEntity.noContent().build();
    }
}