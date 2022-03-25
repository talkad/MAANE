package Communication.UserPersistency.service;

import Communication.UserPersistency.UserInfo;

public interface UserInfoService {

     UserInfo saveUserInfo(UserInfo userInfo);
     UserInfo getUser(String username);

}
