package Communication.Resource;

import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Service.Interfaces.SurveyService;
import Service.SurveyServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/survey")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SurveyController {

    private final SurveyService service = SurveyServiceImpl.getInstance();

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Response<Integer>> createSurvey(@RequestBody Map<String, Object> body){
        return ResponseEntity.ok(
                service.createSurvey((String)body.get("username"), (SurveyDTO) body.get("surveyDTO"))
        );
    }

    @RequestMapping(value = "/submitAnswers", method = RequestMethod.POST)
    public ResponseEntity<Response<Boolean>> addAnswers(@RequestBody SurveyAnswersDTO answers){
        return ResponseEntity.ok()
                .body(service.addAnswers(answers));
    }

    @GetMapping("/get/surveyID={surveyID}")
    public ResponseEntity<Response<SurveyDTO>> getSurvey(@PathVariable("surveyID") int surveyID){
        return ResponseEntity.ok()
                .body(service.getSurvey(surveyID));
    }

    // TODO - define a rule
    @RequestMapping(value ="/addRule",  method = RequestMethod.POST)
    public ResponseEntity<Response<Boolean>> addRule(@RequestBody Map<String, Object> body){
        return ResponseEntity.ok()
                .body( service.addRule((String)body.get("username"), (Integer)body.get("surveyID"), (Rule)body.get("rule"), (Integer)body.get("goalID")));
    }

    @GetMapping("/detectFault/username={username}&surveyID={surveyID}")
    public ResponseEntity<Response<List<List<String>>>> detectFault(@PathVariable("username") String username, @PathVariable("surveyID") int surveyID){
        return ResponseEntity.ok()
                .body(service.detectFault(username, surveyID));
    }


}
