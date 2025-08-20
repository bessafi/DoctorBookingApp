package shujaa.authentication_with_spring.security.service.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import shujaa.authentication_with_spring.security.domain.doctor.Doctor;
import shujaa.authentication_with_spring.security.domain.doctor.DoctorRepository;
import shujaa.authentication_with_spring.security.entity.User;
import shujaa.authentication_with_spring.security.repository.IUserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final IUserRepository userRepository;


    public Doctor findOrCreateDoctor(String googleId, String email, String name, String picture) {
        return doctorRepository.findByEmail(email)
                .orElseGet(() -> {
                    Doctor doctor = new Doctor();
                    doctor.setGoogleId(googleId);
                    doctor.setEmail(email);
                    doctor.setFirstName(name);
                    doctor.setPicture(picture);
                    return doctorRepository.save(doctor);
                });
    }


    public Doctor getOrCreateForCurrentUser() {
        User user = getCurrentUser();
        return doctorRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Doctor d = new Doctor();
                    d.setUser(user);
                    return doctorRepository.save(d);
                });
    }

    public Doctor getByIdOrThrow(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found: " + id));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = (auth != null ? auth.getName() : null);
        if (name == null) throw new NoSuchElementException("No authenticated user");
        // try email first, then username
        return userRepository.findByEmail(name)
                .or(() -> userRepository.findByUsername(name))
                .orElseThrow(() -> new NoSuchElementException("User not found for principal: " + name));
    }
}
