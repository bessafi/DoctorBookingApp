package shujaa.authentication_with_spring.security.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import shujaa.authentication_with_spring.security.controller.response.ApiResponse;
import shujaa.authentication_with_spring.security.controller.response.LoginResponse;
import shujaa.authentication_with_spring.security.dto.LoginUserDto;
import shujaa.authentication_with_spring.security.dto.RegisterUserDto;
import shujaa.authentication_with_spring.security.dto.request.VerifyRequest;
import shujaa.authentication_with_spring.security.service.IAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IAuthentication iAuthentication;

    @Value("${app.redirect.uri.app}")
    private String redirectUriBase;

    public AuthController(IAuthentication iAuthentication) {
        this.iAuthentication = iAuthentication;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDto loginUserDto) {
        LoginResponse loginResponse = iAuthentication.login(loginUserDto);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginResponse);
    }

    @GetMapping("/oauth-login")
    public void oauthLogin(OAuth2AuthenticationToken authentication, HttpServletResponse response) throws IOException {
        try {
            String token = iAuthentication.handleOAuthLogin(authentication);
            response.sendRedirect(redirectUriBase + "?token=" + token);
        } catch (IllegalArgumentException e) {
            response.sendRedirect(redirectUriBase + "?error=" + e.getMessage());
        } catch (Exception e) {
            response.sendRedirect(redirectUriBase + "?error=OAuth login failed");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse> signup(@RequestBody RegisterUserDto registerUserDto) {
        iAuthentication.signup(registerUserDto);
        ApiResponse apiResponse = new ApiResponse("Code sent");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/verify")
    public ResponseEntity<ApiResponse> verifyUser(@RequestBody VerifyRequest request) {
        iAuthentication.verifyUser(request.getEmail(), request.getVerificationCode());
        ApiResponse apiResponse = new ApiResponse("Account verified successfully");
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/resend-code")
    public ResponseEntity<ApiResponse> resendVerificationCode(@RequestParam String email) {
        iAuthentication.resendVerificationCode(email);
        ApiResponse apiResponse = new ApiResponse("Verification code resent");
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/reset-password-request")
    public ResponseEntity<ApiResponse> requestPasswordReset(@RequestParam String email) {
        iAuthentication.requestPasswordReset(email);
        ApiResponse apiResponse = new ApiResponse("Password reset email sent");
        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String email, @RequestParam String verificationCode, @RequestParam String newPassword) {
        iAuthentication.resetPassword(email, verificationCode, newPassword);
        ApiResponse apiResponse = new ApiResponse("Password reset successful");
        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Invalid token"));
        }
        String token = authorizationHeader.substring(7);
        iAuthentication.logout(token);
        return ResponseEntity.ok(new ApiResponse("Logout successful"));
    }
}
