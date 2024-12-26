package shujaa.authentication_with_spring.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shujaa.authentication_with_spring.security.entity.UserRole;


@Repository
public interface IUserRoleRepository extends JpaRepository<UserRole, Long> {
}
