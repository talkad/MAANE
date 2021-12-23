package Domain.UsersManagment;

public class Supervisor extends Instructor{

    public Supervisor() {
        super();
        allowedFunctions.add(Permissions.ASSIGN_SCHOOLS_TO_USER);
        allowedFunctions.add(Permissions.REMOVE_SCHOOLS_FROM_USER);
        allowedFunctions.add(Permissions.REGISTER_USER);
        allowedFunctions.add(Permissions.REMOVE_USER);
        allowedFunctions.add(Permissions.VIEW_INSTRUCTORS_INFO);
        allowedFunctions.add(Permissions.SURVEY_MANAGEMENT);
        allowedFunctions.add(Permissions.GENERATE_WORK_PLAN);
//        allowedFunctions.add(Permissions.PUBLISH_SURVEY);
//        allowedFunctions.add(Permissions.CREATE_SURVEY);
//        allowedFunctions.add(Permissions.REMOVE_SURVEY);
        allowedFunctions.add(Permissions.ADD_GOALS);
        allowedFunctions.add(Permissions.GET_GOALS);
        allowedFunctions.add(Permissions.CHANGE_PASSWORD_TO_USER);
    }

    @Override
    public boolean allowed(Permissions func, User user) {
        return this.allowedFunctions.contains(func);
    }


    @Override
    public UserStateEnum getStateEnum() {
        return UserStateEnum.SUPERVISOR;
    }
}
