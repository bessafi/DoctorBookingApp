package shujaa.authentication_with_spring.security.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shujaa.authentication_with_spring.security.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;



import java.util.List;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
public class SecurityConfiguration {

   
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

   

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(registry -> registry
                        // Auth endpoints + OAuth callbacks
                        .requestMatchers("/auth/**","/api/public/**", "/oauth2/**", "/login/**",
                        "/", "/index.xhtml", "/login.xhtml", "/dashboard.xhtml",
                                         "/javax.faces.resource/**",   // JSF resources (css, js, images)
                                         "/oauth2/**").permitAll()
                        // Botpress-facing endpoints (public):
                        .requestMatchers(HttpMethod.GET, "/availability/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/bookings").permitAll()
                        // Google OAuth redirect handler:
                        .requestMatchers("/calendar/oauth2callback").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("http://localhost:5000/dashboard", true)
            )
            .logout(logout -> logout
                .logoutUrl("/api/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("http://localhost:5000/")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            );
               // .authenticationProvider(authenticationProvider)
               // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        //cfg.setAllowedOriginPatterns(List.of("*")); // or restrict: http://localhost:5173, http://localhost:5000, etc.
        configuration.setAllowedOrigins(List.of("*")); // Allow all origins for simplicity
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "OPTIONS", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
