package Domain.UsersManagment;

import java.util.List;
import java.util.Vector;

public class Registered extends UserState {

    protected final List<Permissions> allowedFunctions;

    public Registered() {
        this.allowedFunctions = new Vector<>();
        this.allowedFunctions.add(Permissions.LOGOUT);
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
        return UserStateEnum.REGISTERED;
    }
}
