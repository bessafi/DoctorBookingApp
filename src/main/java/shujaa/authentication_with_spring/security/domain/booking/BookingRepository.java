package shujaa.authentication_with_spring.security.domain.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByDoctor_Id(Long doctorId);
    List<Booking> findByDoctor_IdAndStartLessThanEqualAndEndGreaterThanEqual(
            Long doctorId, LocalDateTime end, LocalDateTime start
    );
}