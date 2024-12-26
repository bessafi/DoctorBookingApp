package shujaa.authentication_with_spring.security.service;


import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import shujaa.authentication_with_spring.security.controller.response.LoginResponse;
import shujaa.authentication_with_spring.security.dto.LoginUserDto;
import shujaa.authentication_with_spring.security.dto.RegisterUserDto;
import shujaa.authentication_with_spring.security.entity.User;

public interface IAuthentication {
    LoginResponse login(LoginUserDto loginUserDto);

    void logout(String token);

    User signup(RegisterUserDto registerUserDto);

    void verifyUser(String email, String verificationCode);

    void resendVerificationCode(String email);

    void requestPasswordReset(String email);

    void resetPassword(String email, String verificationCode, String newPassword);

    String handleOAuthLogin(OAuth2AuthenticationToken authentication);
}
