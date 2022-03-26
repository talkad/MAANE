package Communication.UserPersistency.Service;

import Communication.UserPersistency.Entity.UserInfo;

public interface UserInfoService {

     UserInfo saveUserInfo(UserInfo userInfo);
     UserInfo getUser(String username);

}
