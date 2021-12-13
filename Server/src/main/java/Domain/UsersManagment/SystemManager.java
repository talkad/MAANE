package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;

public class SystemManager extends Registered{
    public SystemManager() {
        super();
        allowedFunctions.add(PermissionsEnum.REGISTER_SUPERVISOR);
    }

    @Override
    public boolean allowed(PermissionsEnum func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(PermissionsEnum permission, User user, int schoolId) {
        return user.getSchools().contains(schoolId);
    }

    @Override
    public UserStateEnum getStateEnum(){
        return UserStateEnum.SYSTEM_MANAGER;
    }

}
