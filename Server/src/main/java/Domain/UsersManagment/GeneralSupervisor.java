package Domain.UsersManagment;


public class GeneralSupervisor extends Registered{
    public GeneralSupervisor() {
        super();
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
        return UserStateEnum.GENERAL_SUPERVISOR;
    }
}
