package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;

public class Supervisor extends Instructor{

    public Supervisor() {
        super();
        allowedFunctions.add(PermissionsEnum.ASSIGN_SCHOOLS_TO_USER);
        allowedFunctions.add(PermissionsEnum.REGISTER_USER);
        allowedFunctions.add(PermissionsEnum.REMOVE_USER);
        allowedFunctions.add(PermissionsEnum.VIEW_INSTRUCTORS_INFO);

    }

    @Override
    public boolean allowed(PermissionsEnum func, User user) {
        return this.allowedFunctions.contains(func);
    }


    @Override
    public UserStateEnum getStateEnum() {
        return UserStateEnum.SUPERVISOR;
    }
}
