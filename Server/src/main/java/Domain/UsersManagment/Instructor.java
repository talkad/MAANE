package Domain.UsersManagment;


public class Instructor extends Registered{

    public Instructor() {
        super();
    }


    @Override
    public boolean allowed(Permissions func, User user) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public boolean allowed(Permissions func, User user, String schoolId) {
        return this.allowedFunctions.contains(func);
    }

    @Override
    public UserStateEnum getStateEnum() {
        return UserStateEnum.INSTRUCTOR;
    }
}
