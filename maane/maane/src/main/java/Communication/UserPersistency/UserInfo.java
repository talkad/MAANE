package Communication.UserPersistency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    @Id
    private String username;
    private String password;
    private String role;
}
