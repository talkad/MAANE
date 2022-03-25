package Communication.UserPersistency.service;

import Communication.UserPersistency.UserInfo;
import Communication.UserPersistency.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserInfoServiceImpl implements UserInfoService {
    private final UserRepo repo;

    @Override
    public UserInfo saveUserInfo(UserInfo userInfo) {
        return repo.save(userInfo);
    }

    @Override
    public UserInfo getUser(String username) {
        return repo.getById(username);
    }
}
