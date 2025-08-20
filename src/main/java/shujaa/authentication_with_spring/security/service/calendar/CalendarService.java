package shujaa.authentication_with_spring.security.service.calendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shujaa.authentication_with_spring.security.domain.booking.Booking;
import shujaa.authentication_with_spring.security.domain.doctor.Doctor;
import shujaa.authentication_with_spring.security.domain.doctor.DoctorRepository;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final DoctorRepository doctorRepository;

    @Value("${GOOGLE_CLIENT_ID}")     private String clientId;
    @Value("${GOOGLE_CLIENT_SECRET}") private String clientSecret;

    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Exchange authorization code for tokens and persist on Doctor */
    public void connectDoctorWithAuthCode(Long doctorId, String authCode, String redirectUri) throws Exception {
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        var tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                httpTransport, JSON_FACTORY, clientId, clientSecret, authCode, redirectUri
        ).execute();

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found"));

        doctor.setGoogleAccessToken(tokenResponse.getAccessToken());
        doctor.setGoogleRefreshToken(tokenResponse.getRefreshToken()); // needs 'access_type=offline'
        if (tokenResponse.getExpiresInSeconds() != null) {
            doctor.setGoogleTokenExpiry(Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
        }
        doctorRepository.save(doctor);
    }

    public Calendar getCalendarClient(Doctor doctor) throws Exception {
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build();

        if (doctor.getGoogleAccessToken() != null) {
            credential.setAccessToken(doctor.getGoogleAccessToken());
        }
        if (doctor.getGoogleRefreshToken() != null) {
            credential.setRefreshToken(doctor.getGoogleRefreshToken());
        }

        // refresh if needed
        if (doctor.getGoogleTokenExpiry() != null && doctor.getGoogleTokenExpiry().isBefore(Instant.now().plusSeconds(60))) {
            if (credential.refreshToken()) {
                doctor.setGoogleAccessToken(credential.getAccessToken());
                doctor.setGoogleTokenExpiry(Instant.now().plusSeconds(3500)); // approx
                doctorRepository.save(doctor);
            }
        }

        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName("Doctor Booking")
                .build();
    }

    /** Get busy periods (Google freeBusy) in UTC */
    public List<TimePeriod> getBusyPeriods(Doctor doctor, Instant from, Instant to) throws Exception {
        Calendar svc = getCalendarClient(doctor);
        FreeBusyRequest req = new FreeBusyRequest()
                .setTimeMin(new com.google.api.client.util.DateTime(Date.from(from)))
                .setTimeMax(new com.google.api.client.util.DateTime(Date.from(to)))
                .setItems(List.of(new FreeBusyRequestItem().setId(doctor.getCalendarId())));
        FreeBusyResponse resp = svc.freebusy().query(req).execute();
        FreeBusyCalendar cal = resp.getCalendars().get(doctor.getCalendarId());
        if (cal == null || cal.getBusy() == null) return List.of();
        return cal.getBusy();
    }

    public String createEvent(Doctor doctor, Booking booking) throws Exception {
        Calendar svc = getCalendarClient(doctor);
        Event event = new Event()
                .setSummary("Consultation with " + booking.getPatientName())
                .setDescription(booking.getReason() == null ? "" : booking.getReason());

        ZoneId zone = ZoneId.of(doctor.getTimezone());
        var start = new EventDateTime().setDateTime(
                new com.google.api.client.util.DateTime(
                        Date.from(booking.getStart().atZone(zone).toInstant())
                )).setTimeZone(doctor.getTimezone());

        var end = new EventDateTime().setDateTime(
                new com.google.api.client.util.DateTime(
                        Date.from(booking.getEnd().atZone(zone).toInstant())
                )).setTimeZone(doctor.getTimezone());

        event.setStart(start);
        event.setEnd(end);
        event.setAttendees(List.of(new EventAttendee().setEmail(booking.getPatientEmail()).setDisplayName(booking.getPatientName())));
        Event created = svc.events().insert(doctor.getCalendarId(), event).execute();
        return created.getId();
    }

    public void deleteEvent(Doctor doctor, String eventId) throws Exception {
        if (eventId == null || eventId.isBlank()) return;
        Calendar svc = getCalendarClient(doctor);
        svc.events().delete(doctor.getCalendarId(), eventId).execute();
    }
}
