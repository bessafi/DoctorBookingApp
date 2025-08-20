package shujaa.authentication_with_spring.security.service.availability;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shujaa.authentication_with_spring.security.domain.availability.AvailabilityBlockRepository;
import shujaa.authentication_with_spring.security.domain.doctor.Doctor;
import shujaa.authentication_with_spring.security.service.calendar.CalendarService;
import shujaa.authentication_with_spring.security.web.dto.AvailabilitySlot;

import com.google.api.services.calendar.model.TimePeriod;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityBlockRepository blockRepository;
    private final CalendarService calendarService;

    /**
     * Compute free slots between [from,to) in doctor's local time, considering:
     * - Working hours
     * - Google Calendar busy events
     * - DB blocks
     */
    public List<AvailabilitySlot> computeAvailableSlots(Doctor doctor, LocalDate fromDate, LocalDate toDate) throws Exception {
        ZoneId zone = ZoneId.of(doctor.getTimezone());
        LocalTime workStart = doctor.getWorkStart();
        LocalTime workEnd   = doctor.getWorkEnd();
        int slotMinutes     = doctor.getSlotMinutes() == null ? 30 : doctor.getSlotMinutes();

        LocalDateTime fromLocal = LocalDateTime.of(fromDate, LocalTime.MIN);
        LocalDateTime toLocal   = LocalDateTime.of(toDate, LocalTime.MAX);

        Instant from = fromLocal.atZone(zone).toInstant();
        Instant to   = toLocal.atZone(zone).toInstant();

        // Busy from Google
        List<TimePeriod> googleBusy = calendarService.getBusyPeriods(doctor, from, to);

        // DB blocks
        var dbBlocks = blockRepository.findByDoctor_IdAndStartLessThanEqualAndEndGreaterThanEqual(
                doctor.getId(), toLocal, fromLocal
        );

        // Build per-day schedule of free slots
        List<AvailabilitySlot> result = new ArrayList<>();
        for (LocalDate day = fromDate; !day.isAfter(toDate); day = day.plusDays(1)) {
            LocalDateTime dayStart = LocalDateTime.of(day, workStart);
            LocalDateTime dayEnd   = LocalDateTime.of(day, workEnd);
            if (!dayStart.isBefore(dayEnd)) continue;

            // Start from entire working period, subtract busy chunks
            List<AvailabilitySlot> free = List.of(new AvailabilitySlot(dayStart, dayEnd));

            // Subtract Google busy
            for (TimePeriod tp : googleBusy) {
                LocalDateTime bStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(tp.getStart().getValue()), zone);
                LocalDateTime bEnd   = LocalDateTime.ofInstant(Instant.ofEpochMilli(tp.getEnd().getValue()), zone);
                free = subtract(free, new AvailabilitySlot(bStart, bEnd));
            }
            // Subtract DB blocks
            for (var block : dbBlocks) {
                free = subtract(free, new AvailabilitySlot(block.getStart(), block.getEnd()));
            }

            // Split into slot-sized windows
            for (AvailabilitySlot f : free) {
                LocalDateTime s = f.getStart();
                while (!s.plusMinutes(slotMinutes).isAfter(f.getEnd())) {
                    result.add(new AvailabilitySlot(s, s.plusMinutes(slotMinutes)));
                    s = s.plusMinutes(slotMinutes);
                }
            }
        }

        // Remove past slots in doctor's time
        LocalDateTime nowLocal = LocalDateTime.now(zone);
        return result.stream()
                .filter(s -> s.getStart().isAfter(nowLocal))
                .sorted(Comparator.comparing(AvailabilitySlot::getStart))
                .collect(Collectors.toList());
    }

    private static List<AvailabilitySlot> subtract(List<AvailabilitySlot> free, AvailabilitySlot busy) {
        List<AvailabilitySlot> out = new ArrayList<>();
        for (AvailabilitySlot f : free) {
            if (busy.getEnd().isBefore(f.getStart()) || busy.getStart().isAfter(f.getEnd())) {
                out.add(f); // no overlap
                continue;
            }
            // overlap cases
            if (busy.getStart().isAfter(f.getStart())) {
                out.add(new AvailabilitySlot(f.getStart(), busy.getStart()));
            }
            if (busy.getEnd().isBefore(f.getEnd())) {
                out.add(new AvailabilitySlot(busy.getEnd(), f.getEnd()));
            }
        }
        return out;
    }
}