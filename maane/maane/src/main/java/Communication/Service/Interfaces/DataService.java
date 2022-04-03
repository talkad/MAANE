package Communication.Service.Interfaces;

import Domain.CommonClasses.Response;

public interface DataService {
    Response<Boolean> assignCoordinator(String currUser, String workField, String firstName, String lastName, String email, String phoneNumber, String school);

    Response<Boolean> removeCoordinator(String currUser, String workField, String school);
}
