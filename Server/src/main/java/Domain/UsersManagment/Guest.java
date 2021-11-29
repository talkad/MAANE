package Domain.UsersManagment;

import Domain.CommonClasses.Response;

import java.util.List;

public class Guest extends User{

    public Guest(){
        //this.allowedFunctions = new Vector<>();
        //this.allowedFunctions.add(PermissionsEnum.REGISTER);
    }

    @Override
    public Response<Boolean> registerUser(String username, String password, UserStateEnum registerUserStateEnum) {
        return new Response<>(false, true, "user is not allowed to register users");
    }

    @Override
    public Response<Boolean> removeUser(String username) {
        return new Response<>(false, true, "user is not allowed to remove users");
    }

    @Override
    public Response<Boolean> assignSchoolsToUser(String userToAssign, List<Integer> schools) {
        return null;//todo
    }

}
