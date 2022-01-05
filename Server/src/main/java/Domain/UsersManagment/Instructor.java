package Domain.UsersManagment;


public class Instructor extends Registered{

    public Instructor() {
        super();
        this.allowedFunctions.add(Permissions.VIEW_WORK_PLAN);

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
