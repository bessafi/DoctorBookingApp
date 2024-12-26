package shujaa.authentication_with_spring.security.utils;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendVerificationEmail(String to, String subject, String text) throws MessagingException;
}
