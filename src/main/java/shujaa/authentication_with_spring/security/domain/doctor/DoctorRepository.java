package shujaa.authentication_with_spring.security.domain.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser_Id(Long userId);
    Optional<Doctor> findByEmail(String email);
}
