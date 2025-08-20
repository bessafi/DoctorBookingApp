package shujaa.authentication_with_spring.security.domain.availability;

import jakarta.persistence.*;
import lombok.*;
import shujaa.authentication_with_spring.security.domain.doctor.Doctor;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "availability_blocks", indexes = {
        @Index(name="idx_av_doctor_start", columnList = "doctor_id,start_time"),
        @Index(name="idx_av_doctor_end", columnList = "doctor_id,end_time")
})
public class AvailabilityBlock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    private String reason; // optional note
}