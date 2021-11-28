package UsersManagment;

import CommonClasses.Response;

public class SystemManager extends User{

//    public SystemManager(String username) {
//        this.name = username
//    }

    @Override
    public Response<Boolean> registerUser(String username, String password, UserStateEnum registerUserStateEnum) {
        return new Response<>(true, false, "successfully registered a new user");
    }

    public Response<Boolean> removeUser(String username){
        return new Response<>(true, false, "successfully removed the user");//todo remove all that users responsibility?
    }
}
