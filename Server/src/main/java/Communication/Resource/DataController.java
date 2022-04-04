package Communication.Resource;

import Domain.CommonClasses.Response;
import Service.DataServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/data")


public class DataController {
    ObjectMapper objectMapper = new ObjectMapper();
    private Gson gson = new Gson();
    private static final DataServiceImpl service = DataServiceImpl.getInstance();

    @RequestMapping(value = "/assignCoordinator", method = RequestMethod.POST)//todo aviad, tal
    public ResponseEntity<Response<Boolean>> assignCoordinator(@RequestBody Map<String, Object> body){
        return ResponseEntity.ok()
                .body(service.assignCoordinator((String)body.get("currUser"), (String)body.get("workField"), (String)body.get("firstName"), (String)body.get("lastName"), (String)body.get("email"), (String)body.get("phoneNumber"), (String)body.get("school")));
    }

    @RequestMapping(value = "/removeCoordinator", method = RequestMethod.POST)//todo aviad, tal
    public ResponseEntity<Response<Boolean>> removeCoordinator(@RequestBody Map<String, Object>  body){
        return ResponseEntity.ok()
                .body(service.removeCoordinator((String)body.get("currUser"), (String)body.get("workField"), (String)body.get("school")));
    }
}
