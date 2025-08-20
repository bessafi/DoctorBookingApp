package shujaa.authentication_with_spring.security.web.dto;

import java.time.LocalDateTime;

public class AvailabilitySlot {
    private LocalDateTime start;
    private LocalDateTime end;
    public AvailabilitySlot() {}
    public AvailabilitySlot(LocalDateTime start, LocalDateTime end) {
        this.start = start; this.end = end;
    }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
}
