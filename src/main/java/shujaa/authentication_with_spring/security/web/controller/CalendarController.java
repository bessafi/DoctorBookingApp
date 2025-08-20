package shujaa.authentication_with_spring.security.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shujaa.authentication_with_spring.security.domain.doctor.Doctor;
import shujaa.authentication_with_spring.security.service.calendar.CalendarService;
import shujaa.authentication_with_spring.security.service.doctor.DoctorService;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final DoctorService doctorService;
    private final CalendarService calendarService;

    @Value("${GOOGLE_REDIRECT_URI:http://localhost:8080/calendar/oauth2callback}")
    private String redirectUri;

    /** Build the Google OAuth consent URL (auth code flow) */
    @GetMapping("/connect")
    public ResponseEntity<Void> connect(@RequestParam Long doctorId) {
        String scope = URLEncoder.encode("https://www.googleapis.com/auth/calendar", StandardCharsets.UTF_8);
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + System.getenv("GOOGLE_CLIENT_ID")
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&response_type=code&access_type=offline&prompt=consent"
                + "&scope=" + scope
                + "&state=" + doctorId;
        return ResponseEntity.status(302).location(URI.create(authUrl)).build();
    }

    /** OAuth redirect URI (add this URI in your Google Cloud console) */
    @GetMapping("/oauth2callback")
    public ResponseEntity<String> oauthCallback(@RequestParam("code") String code,
                                                @RequestParam("state") Long doctorId) throws Exception {
        calendarService.connectDoctorWithAuthCode(doctorId, code, redirectUri);
        return ResponseEntity.ok("Google Calendar connected for doctor " + doctorId);
    }
}