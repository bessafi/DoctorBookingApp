package shujaa.authentication_with_spring.security.service.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shujaa.authentication_with_spring.security.domain.availability.AvailabilityBlockRepository;
import shujaa.authentication_with_spring.security.domain.booking.*;
import shujaa.authentication_with_spring.security.domain.doctor.Doctor;
import shujaa.authentication_with_spring.security.service.calendar.CalendarService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AvailabilityBlockRepository blockRepository;
    private final CalendarService calendarService;

    public List<Booking> listByDoctor(Long doctorId) {
        return bookingRepository.findByDoctor_Id(doctorId);
    }

    @Transactional
    public Booking create(Doctor doctor, Booking draft) throws Exception {
        ensureFree(doctor, draft.getStart(), draft.getEnd());
        // Persist first
        Booking saved = bookingRepository.save(draft);
        // Create event in Google
        String eventId = calendarService.createEvent(doctor, saved);
        saved.setGoogleEventId(eventId);
        return bookingRepository.save(saved);
    }

    @Transactional
    public Booking update(Doctor doctor, Long bookingId, LocalDateTime newStart, LocalDateTime newEnd, String reason) throws Exception {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found: " + bookingId));
        ensureFree(doctor, newStart, newEnd, bookingId);
        b.setStart(newStart);
        b.setEnd(newEnd);
        if (reason != null) b.setReason(reason);
        // Simplest path: delete + recreate event
        calendarService.deleteEvent(doctor, b.getGoogleEventId());
        String newEventId = calendarService.createEvent(doctor, b);
        b.setGoogleEventId(newEventId);
        return bookingRepository.save(b);
    }

    @Transactional
    public void cancel(Doctor doctor, Long bookingId) throws Exception {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found: " + bookingId));
        b.setStatus(BookingStatus.CANCELED);
        calendarService.deleteEvent(doctor, b.getGoogleEventId());
        bookingRepository.save(b);
    }

    private void ensureFree(Doctor doctor, LocalDateTime start, LocalDateTime end) {
        ensureFree(doctor, start, end, null);
    }

    private void ensureFree(Doctor doctor, LocalDateTime start, LocalDateTime end, Long excludeBookingId) {
        // Booking overlap
        List<Booking> overlaps = bookingRepository
                .findByDoctor_IdAndStartLessThanEqualAndEndGreaterThanEqual(doctor.getId(), end, start);
        if (excludeBookingId != null) overlaps.removeIf(b -> b.getId().equals(excludeBookingId));
        if (!overlaps.isEmpty()) throw new IllegalStateException("Slot already booked");

        // Availability blocks
        var blocks = blockRepository
                .findByDoctor_IdAndStartLessThanEqualAndEndGreaterThanEqual(doctor.getId(), end, start);
        if (!blocks.isEmpty()) throw new IllegalStateException("Doctor is unavailable in that period");
    }
}
