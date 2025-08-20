package shujaa.authentication_with_spring.security.domain.booking;

import jakarta.persistence.*;
import lombok.*;
import shujaa.authentication_with_spring.security.domain.doctor.Doctor;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "bookings", indexes = {
        @Index(name="idx_booking_doctor_start", columnList = "doctor_id,start_time"),
        @Index(name="idx_booking_doctor_end", columnList = "doctor_id,end_time")
})
public class Booking {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    private String patientName;
    private String patientEmail;
    private String reason;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.CONFIRMED;

    /** Google Calendar event id for synchronization */
    private String googleEventId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
