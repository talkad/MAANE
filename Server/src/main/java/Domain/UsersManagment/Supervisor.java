package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Vector;

public class Supervisor extends Instructor{

    public Supervisor() {
        super();
        allowedFunctions.add(PermissionsEnum.ASSIGNSCHOOLSTOUSER);
        allowedFunctions.add(PermissionsEnum.REGISTERUSER);
    }

    @Override
    public UserStateEnum getStateEnum() {
        return UserStateEnum.SUPERVISOR;
    }
}
