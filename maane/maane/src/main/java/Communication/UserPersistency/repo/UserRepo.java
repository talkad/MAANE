package Communication.UserPersistency.repo;

import Communication.UserPersistency.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserInfo, String> {

    UserInfo findByUsername(String username);

}
