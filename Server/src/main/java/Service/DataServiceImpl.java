package Service;

import Domain.CommonClasses.Response;
import Domain.DataManagement.DataController;
import Service.Interfaces.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class DataServiceImpl implements DataService {

    private static class CreateSafeThreadSingleton {
        private static final DataServiceImpl INSTANCE = new DataServiceImpl();
    }

    public static DataServiceImpl getInstance() {
        return DataServiceImpl.CreateSafeThreadSingleton.INSTANCE;
    }

    @Override
    public Response<Boolean> assignCoordinator(String currUser, String workField, String firstName, String lastName, String email, String phoneNumber, String school) {
        Response<Boolean> res = DataController.getInstance().assignCoordinator(currUser, workField, firstName, lastName, email, phoneNumber, school);

        if (res.isFailure())
            log.error("failed to assign the coordinator {} to the school {} by {}", firstName + " " + lastName, school, currUser);
        else
            log.info("successfully assigned the coordinator {} to the school {} by {}", firstName + " " + lastName, school, currUser);
        return res;
    }

    @Override
    public Response<Boolean> removeCoordinator(String currUser, String workField, String school) {
        Response<Boolean> res = DataController.getInstance().removeCoordinator(currUser, workField, school);

        if (res.isFailure())
            log.error("failed to remove the {} coordinator from the school {} by {}", workField, school, currUser);
        else
            log.info("successfully removed the {} coordinator from the school {} by {}", workField, school, currUser);
        return res;
    }
}
