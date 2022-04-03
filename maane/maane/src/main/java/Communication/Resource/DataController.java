package Communication.Resource;

import Communication.Service.Interfaces.DataService;
import Domain.CommonClasses.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/data")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DataController {

    private final DataService service;
    private final SessionHandler sessionHandler;

    @PostMapping(value = "/assignCoordinator") //todo aviad
    public ResponseEntity<Response<Boolean>> assignCoordinator(@RequestHeader(value = "Authorization") String token, @RequestBody Map<String, Object> body){
        return ResponseEntity.ok()
                .body(service.assignCoordinator(sessionHandler.getUsernameByToken(token).getResult(), (String)body.get("workField"), (String)body.get("firstName"), (String)body.get("lastName"), (String)body.get("email"), (String)body.get("phoneNumber"), (String)body.get("school")));
    }

    @PostMapping(value = "/removeCoordinator") //todo aviad
    public ResponseEntity<Response<Boolean>> removeCoordinator(@RequestHeader(value = "Authorization") String token, @RequestBody Map<String, Object> body){
        return ResponseEntity.ok()
                .body(service.removeCoordinator(sessionHandler.getUsernameByToken(token).getResult(), (String)body.get("workField"), (String)body.get("school")));
    }
}

