package exe.SonMaiHeritage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminLoginResponse {
    private boolean success;
    private String username;
    private String token;
    private String message;
}
