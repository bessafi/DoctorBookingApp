package shujaa.authentication_with_spring.security.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeResponse {
    private String email;
    private String name;
    private String picture;
    private boolean authenticated;
}
