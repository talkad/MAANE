package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;

public class Instructor extends Registered{

    public Instructor() {
        super();
    }


    @Override
    public boolean allowed(PermissionsEnum func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(PermissionsEnum func, User user, int schoolId) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public UserStateEnum getStateEnum() {
        return UserStateEnum.INSTRUCTOR;
    }
}
