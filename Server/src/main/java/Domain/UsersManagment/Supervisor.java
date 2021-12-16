package Domain.UsersManagment;

public class Supervisor extends Instructor{

    public Supervisor() {
        super();
        allowedFunctions.add(PermissionsEnum.ASSIGN_SCHOOLS_TO_USER);
        allowedFunctions.add(PermissionsEnum.REMOVE_SCHOOLS_FROM_USER);
        allowedFunctions.add(PermissionsEnum.REGISTER_USER);
        allowedFunctions.add(PermissionsEnum.REMOVE_USER);
        allowedFunctions.add(PermissionsEnum.VIEW_INSTRUCTORS_INFO);
        allowedFunctions.add(PermissionsEnum.CREATE_SURVEY);
        allowedFunctions.add(PermissionsEnum.REMOVE_SURVEY);
        allowedFunctions.add(PermissionsEnum.ADD_GOALS);
        allowedFunctions.add(PermissionsEnum.GET_GOALS);


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
