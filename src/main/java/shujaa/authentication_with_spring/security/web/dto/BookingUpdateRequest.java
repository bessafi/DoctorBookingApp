package shujaa.authentication_with_spring.security.web.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class BookingUpdateRequest {
    @NotNull private LocalDateTime start;
    @NotNull private LocalDateTime end;
    private String reason;
    // getters/setters

     public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
