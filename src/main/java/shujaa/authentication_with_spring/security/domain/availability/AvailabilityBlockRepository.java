package shujaa.authentication_with_spring.security.domain.availability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilityBlockRepository extends JpaRepository<AvailabilityBlock, Long> {
    List<AvailabilityBlock> findByDoctor_IdAndStartLessThanEqualAndEndGreaterThanEqual(
            Long doctorId, LocalDateTime end, LocalDateTime start
    );
}
