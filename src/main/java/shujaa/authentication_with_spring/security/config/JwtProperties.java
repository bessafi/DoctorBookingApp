package shujaa.authentication_with_spring.security.config; // Or your config package

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.jwt")
@Data
public class JwtProperties {
    private String secretKey;
    private long expirationTime;
}
