package shujaa.authentication_with_spring.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
public class OAuth2ClientConfig {

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    @Value("${GITHUB_CLIENT_ID}")
    private String githubClientId;

    @Value("${GITHUB_CLIENT_SECRET}")
    private String githubClientSecret;

    @Value("${REDIRECT_URI_BASE}")
    private String redirectUriBase;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
                googleClientRegistration(),
                githubClientRegistration()
        );
    }

   /*  private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                //.scope("openid", "profile", "email")
                .scope("openid", "profile", "email", "https://www.googleapis.com/auth/calendar")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .clientName("Google")
                //.redirectUri(redirectUriBase + "/login/oauth2/code/google")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .build();
    }*/

    private ClientRegistration googleClientRegistration() {
    return ClientRegistration.withRegistrationId("google")
            .clientId(googleClientId)
            .clientSecret(googleClientSecret)
            .scope("openid", "profile", "email", "https://www.googleapis.com/auth/calendar")
            .authorizationUri("https://accounts.google.com/o/oauth2/auth")
            .tokenUri("https://oauth2.googleapis.com/token")
            .clientName("Google")
            .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
            .userNameAttributeName("sub")
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")  // ✅ FIXED
            .build();
}

    private ClientRegistration githubClientRegistration() {
        return ClientRegistration.withRegistrationId("github")
                .clientId(githubClientId)
                .clientSecret(githubClientSecret)
                .scope("read:user", "user:email")
                .authorizationUri("https://github.com/login/oauth/authorize")
                .tokenUri("https://github.com/login/oauth/access_token")
                .userInfoUri("https://api.github.com/user")
                .clientName("GitHub")
                .redirectUri(redirectUriBase + "/login/oauth2/code/github")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .userNameAttributeName("login")
                .build();
    }
}
