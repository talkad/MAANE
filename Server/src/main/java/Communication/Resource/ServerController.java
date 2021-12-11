package Communication.Resource;

import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Domain.DataManagement.Survey;
import Service.ServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/MAANE")
public class ServerController {

    private final ServerService service = ServerService.getInstance();

    @PostMapping("/survey/create")
    public ResponseEntity<Response<Integer>> createSurvey(@RequestBody Map<String, String> body){
        return ResponseEntity.ok(
                service.createSurvey(body.get("username"), body.get("title"))
        );
    }

    @PostMapping("/survey/remove")
    public ResponseEntity<Response<Boolean>> removeSurvey(@RequestBody Map<String, Object> body){
        return ResponseEntity.ok(
                service.removeSurvey((String)body.get("username"), (Integer)body.get("id"))
        );
    }

    @GetMapping("/survey/get/{id}")
    public ResponseEntity<Response<Survey>> getSurvey(@PathVariable("id") int id){
        return ResponseEntity.ok(
                service.getSurvey(id)
        );
    }

    @PostMapping("/survey/publish")
    public ResponseEntity<Response<Survey>> publishSurvey(@RequestBody Map<String, String> body){
        return ResponseEntity.ok(
                service.publishSurvey(body.get("username"))
        );
    }

    @PostMapping("/survey/addQuestion")
    public ResponseEntity<Response<Integer>> addQuestion(@RequestBody Map<String, Object> body){
        return ResponseEntity.ok(
                service.addQuestion((Integer)body.get("id"), (String)body.get("questionText"))
        );
    }

    @PostMapping("/survey/removeQuestion")
    public ResponseEntity<Response<Boolean>> removeQuestion(@RequestBody Map<String, Integer> body){
        return ResponseEntity.ok(
                service.removeQuestion(body.get("id"), body.get("questionID"))
        );
    }

    @PostMapping("/survey/addAnswer")
    public ResponseEntity<Response<Integer>> addAnswer(@RequestBody Map<String, Object> body){
        return ResponseEntity.ok(
                service.addAnswer((Integer)body.get("id"), (Integer)body.get("questionID"), (String)body.get("answer"))
        );
    }

    @PostMapping("/survey/removeAnswer")
    public ResponseEntity<Response<Boolean>> removeAnswer(@RequestBody Map<String, Integer> body){
        return ResponseEntity.ok(
                service.removeAnswer(body.get("id"), body.get("questionID"), body.get("answerID"))
        );
    }

    @PostMapping("/survey/addRule")
    public ResponseEntity<Response<Boolean>> addRule(@RequestBody Map<String, Object> body){
        return ResponseEntity.ok(
                service.addRule((String)body.get("username"), (Integer)body.get("id"), (Rule)body.get("rule"), (String)body.get("description"))
        );
    }

    @PostMapping("/survey/removeRule")
    public ResponseEntity<Response<Boolean>> removeRule(@RequestBody Map<String, Object> body){
        return ResponseEntity.ok(
                service.removeRule((String)body.get("username"), (Integer)body.get("id"), (Integer)body.get("index"))
        );
    }

    @GetMapping("/survey/detectFault/{username}-{id}")
    public ResponseEntity<Response<List<String>>> detectFault(@PathVariable("username") String username, @PathVariable("id") int id){
        return ResponseEntity.ok(
                service.detectFault(username, id)
        );
    }

}
