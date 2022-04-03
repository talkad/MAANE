package AcceptanceTesting.Bridge;

import Communication.Service.Interfaces.DataService;
import Domain.CommonClasses.Response;

public class ProxyBridgeData implements DataService {//do tests for this class
    private DataService real;

    public ProxyBridgeData(){
        real = null;
    }

    public void setRealBridge(DataService implementation) {
        if(real == null){
            real = implementation;
        }
    }

    @Override
    public Response<Boolean> assignCoordinator(String currUser, String workField, String firstName, String lastName, String email, String phoneNumber, String school) {
        if (real != null){
            return real.assignCoordinator(currUser, workField, firstName, lastName, email, phoneNumber, school);
        }

        return new Response<>(null, true, "not implemented");
    }

    @Override
    public Response<Boolean> removeCoordinator(String currUser, String workField, String school) {
        if (real != null){
            return real.removeCoordinator(currUser, workField, school);
        }

        return new Response<>(null, true, "not implemented");
    }
}
