package Communication.Resource;

import Communication.DTOs.RuleDTO;
import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Communication.DTOs.SurveyDetailsDTO;
import Communication.Service.Interfaces.SurveyService;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Domain.DataManagement.FaultDetector.Rules.RuleConverter;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/survey")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SurveyController {

    private final Gson gson;
    private final SurveyService service;
    private final SessionHandler sessionHandler;

    @PostMapping(value = "/createSurvey")
    public ResponseEntity<Response<String>> createSurvey(@RequestHeader(value = "Authorization") String token, @RequestBody SurveyDTO surveyDTO){
        return ResponseEntity.ok(
                service.createSurvey(sessionHandler.getUsernameByToken(token).getResult(), surveyDTO)
        );
    }

    @PostMapping(value = "/submitAnswers")
    public ResponseEntity<Response<Boolean>> addAnswers(@RequestBody SurveyAnswersDTO answers){

        return ResponseEntity.ok()
                .body(service.addAnswers(answers));
    }

    @GetMapping("/getSurvey/surveyID={surveyID}")
    public ResponseEntity<Response<SurveyDTO>> getSurvey(@PathVariable("surveyID") String surveyID){
        return ResponseEntity.ok()
                .body(service.getSurvey(surveyID));
    }

    @PostMapping(value ="/addRule")
    public ResponseEntity<Response<Boolean>> addRule(@RequestHeader(value = "Authorization") String token, @RequestBody Map<String, Object> body){
        RuleDTO ruleDTO = gson.fromJson((String)body.get("rule"), RuleDTO.class);
        Rule rule = RuleConverter.getInstance().convertRule(ruleDTO);

        if(rule == null)
            return ResponseEntity.ok()
                    .body(new Response<>(false, true, "rule converter failed"));

        return ResponseEntity.ok()
                .body( service.addRule(sessionHandler.getUsernameByToken(token).getResult(), (String)body.get("surveyID"), rule, (Integer)body.get("goalID")));
    }

    @PostMapping(value = "/removeRule")
    public ResponseEntity<Response<Boolean>> removeRule(@RequestHeader(value = "Authorization") String token, @RequestBody Map<String, Object> body){

        return ResponseEntity.ok()
                .body(service.removeRule(sessionHandler.getUsernameByToken(token).getResult(), (String)body.get("surveyID"), (Integer)body.get("ruleID")));
    }

    @GetMapping("/detectFault/surveyID={surveyID}&year={year}")
    public ResponseEntity<Response<List<List<String>>>> detectFault(@RequestHeader(value = "Authorization") String token, @PathVariable("surveyID") String surveyID, @PathVariable("year") String year){
        return ResponseEntity.ok()
                .body(service.detectFault(sessionHandler.getUsernameByToken(token).getResult(), surveyID, year));
    }

    @GetMapping("/getSurveys")
    public ResponseEntity<Response<List<SurveyDetailsDTO>>> getSurveys(@RequestHeader(value = "Authorization") String token){
        return ResponseEntity.ok()
                .body(service.getSurveys(sessionHandler.getUsernameByToken(token).getResult()));
    }

    @GetMapping("/getRules/ruleID={ruleID}")
    public ResponseEntity<Response<List<Rule>>> getRules(@PathVariable("ruleID") String ruleID){
        return ResponseEntity.ok()
                .body(service.getRules(ruleID));
    }


}
