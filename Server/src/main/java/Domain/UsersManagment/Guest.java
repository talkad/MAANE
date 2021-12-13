package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;

public class Guest extends UserState{
    private final List<PermissionsEnum> allowedFunctions;

    public Guest(){
        this.allowedFunctions = new Vector<>();
        this.allowedFunctions.add(PermissionsEnum.LOGIN);
    }

    @Override
    public boolean allowed(PermissionsEnum func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(PermissionsEnum func, User user, int schoolId) {
        return false;
    }

    @Override
    public UserStateEnum getStateEnum() {
        return UserStateEnum.GUEST;
    }
}
