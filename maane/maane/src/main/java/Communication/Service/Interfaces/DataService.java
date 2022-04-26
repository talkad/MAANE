package Communication.Service.Interfaces;

import Domain.CommonClasses.Response;
import Persistence.DbDtos.SchoolDBDTO;

public interface DataService {
    Response<Boolean> assignCoordinator(String currUser, String workField, String firstName, String lastName, String email, String phoneNumber, String school);

    Response<Boolean> removeCoordinator(String currUser, String workField, String school);

    Response<Boolean> insertSchool (SchoolDBDTO school);

    Response<Boolean> removeSchool (String symbol);

    Response<Boolean> updateSchool (String symbol, SchoolDBDTO school);
}
