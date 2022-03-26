package Communication.UserPersistency.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String username;
    private String password;
    private String role;
}
