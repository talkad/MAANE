package Domain.UsersManagment;

public abstract class UserState {

    public abstract boolean allowed(PermissionsEnum func, User user);

    public abstract boolean allowed(PermissionsEnum func, User user, int schoolId);

    public abstract UserStateEnum getStateEnum();

}
