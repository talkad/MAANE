package Domain.UsersManagment;


public class SystemManager extends Registered{
    public SystemManager() {
        super();
        allowedFunctions.add(PermissionsEnum.REGISTER_SUPERVISOR);
        allowedFunctions.add(PermissionsEnum.REMOVE_USER);
        allowedFunctions.add(PermissionsEnum.ASSIGN_SCHOOLS_TO_USER);
        allowedFunctions.add(PermissionsEnum.REGISTER_USER);
        allowedFunctions.add(PermissionsEnum.CHANGE_PASSWORD);
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
