package Domain.UsersManagment;


public class SystemManager extends Registered{
    public SystemManager() {
        super();
        allowedFunctions.add(Permissions.REGISTER_SUPERVISOR);
        allowedFunctions.add(Permissions.REMOVE_USER);
        allowedFunctions.add(Permissions.ASSIGN_SCHOOLS_TO_USER);
        allowedFunctions.add(Permissions.REGISTER_USER);
        allowedFunctions.add(Permissions.CHANGE_PASSWORD_TO_USER);
    }

    @Override
    public boolean allowed(Permissions func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(Permissions permission, User user, int schoolId) {
        return user.getSchools().contains(schoolId);
    }

    @Override
    public UserStateEnum getStateEnum(){
        return UserStateEnum.SYSTEM_MANAGER;
    }

}
