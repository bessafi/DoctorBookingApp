package shujaa.authentication_with_spring.security.web.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class DoctorProfileDto {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String phone;
    @NotBlank private String specialty;
    @NotBlank private String timezone;            // IANA
    @NotBlank private String calendarId;          // usually "primary"
    @NotNull private LocalTime workStart;
    @NotNull private LocalTime workEnd;
    @NotNull @Min(5) @Max(240) private Integer slotMinutes;

    // getters/setters
    // (Use your IDE to generate to keep the class short if you prefer)
     public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }



    public LocalTime getWorkStart() {
        return workStart;
    }

    public void setWorkStart(LocalTime workStart) {
        this.workStart = workStart;
    }

    public LocalTime getWorkEnd() {
        return workEnd;
    }

    public void setWorkEnd(LocalTime workEnd) {
        this.workEnd = workEnd;
    }

    public Integer getSlotMinutes() {
        return slotMinutes;
    }

    public void setSlotMinutes(Integer slotMinutes) {
        this.slotMinutes = slotMinutes;
    }

}
