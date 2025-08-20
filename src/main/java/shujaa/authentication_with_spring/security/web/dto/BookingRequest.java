package shujaa.authentication_with_spring.security.web.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class BookingRequest {
    @NotNull private Long doctorId;
    @NotBlank private String patientName;
    @Email @NotBlank private String patientEmail;
    private String reason;
    @NotNull private LocalDateTime start;
    @NotNull private LocalDateTime end;
  
    // Getters and Setters

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

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
}