package shujaa.authentication_with_spring.security.domain.doctor;

import jakarta.persistence.*;
import lombok.*;
import shujaa.authentication_with_spring.security.entity.User;

import java.time.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "doctors", indexes = {
        @Index(name="idx_doctor_user", columnList = "user_id", unique = true)
})
public class Doctor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String firstName;
    private String lastName;
    private String phone;
    private String specialty;
    private String picture;
    private String googleId;
    private String email;

    /** IANA TZ, e.g., "Africa/Algiers" */
    private String timezone = "Africa/Algiers";

    /** Usually "primary" for the user’s main calendar */
    private String calendarId = "primary";

    /** Google OAuth tokens */
    @Column(length = 2048)
    private String googleAccessToken;

    @Column(length = 2048)
    private String googleRefreshToken;

    private Instant googleTokenExpiry;

    /** Working hours + slot size used to propose slots (local to timezone) */
    private LocalTime workStart = LocalTime.of(9, 0);
    private LocalTime workEnd   = LocalTime.of(17, 0);
    private Integer slotMinutes = 30;

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

     public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
