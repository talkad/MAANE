package AcceptanceTesting.Bridge;

import Communication.Service.Interfaces.DataService;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Persistence.DbDtos.SchoolDBDTO;

import java.util.List;

public class ProxyBridgeData implements DataService {//todo tests for this class
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

    @Override
    public Response<Boolean> insertSchool(SchoolDBDTO school) {
        if (real != null){
            return real.insertSchool(school);
        }

        return new Response<>(null, true, "not implemented");    }

    @Override
    public Response<Boolean> removeSchool(String symbol) {
        if (real != null){
            return real.removeSchool(symbol);
        }

        return new Response<>(null, true, "not implemented");    }

    @Override
    public Response<Boolean> updateSchool(String symbol, SchoolDBDTO school) {
        if (real != null){
            return real.updateSchool(symbol, school);
        }

        return new Response<>(null, true, "not implemented");    }

    @Override
    public Response<SchoolDBDTO> getSchool(String username, String symbol) {
        if (real != null){
            return real.getSchool(username, symbol);
        }

        return new Response<>(null, true, "not implemented");    }

    @Override
    public Response<List<Pair<String, String>>> getUserSchools(String username) {
        if (real != null){
            return real.getUserSchools(username);
        }

        return new Response<>(null, true, "not implemented");    }
}
