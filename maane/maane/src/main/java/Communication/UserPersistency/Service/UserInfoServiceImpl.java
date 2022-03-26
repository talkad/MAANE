package Communication.UserPersistency.Service;

import Communication.UserPersistency.Entity.UserInfo;
import Domain.CommonClasses.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    private final Map<String, Pair<String, String>> repo;

    @Override
    public UserInfo saveUserInfo(UserInfo userInfo) {
        log.info("user {} saved in the connection db", userInfo.getUsername());

        repo.put(userInfo.getUsername(), new Pair<>(userInfo.getRole(), userInfo.getPassword()));
        return userInfo;
    }

    @Override
    public UserInfo getUser(String username) {

        Pair<String, String> data = repo.get(username);
        return new UserInfo(username, data.getFirst(), data.getSecond());
    }
}
