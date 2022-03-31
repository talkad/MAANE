package Communication.Resource;

import Communication.DTOs.RuleDTO;
import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Communication.Service.Interfaces.SurveyService;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
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
        Rule rule = RuleConverter(ruleDTO);

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
    public ResponseEntity<Response<List<String>>> getSurveys(@RequestHeader(value = "Authorization") String token){
        return ResponseEntity.ok()
                .body(service.getSurveys(sessionHandler.getUsernameByToken(token).getResult()));
    }

    @GetMapping("/getRules/ruleID={ruleID}")
    public ResponseEntity<Response<List<Rule>>> getRules(@PathVariable("ruleID") String ruleID){
        return ResponseEntity.ok()
                .body(service.getRules(ruleID));
    }

    private Rule RuleConverter(RuleDTO ruleDTO) {
        Rule rule;

        switch (ruleDTO.getType()){
            case AND:
                rule = ANDRuleConverter(ruleDTO);
                break;
            case OR:
                rule = OrRuleConverter(ruleDTO);
                break;
            case IFF:
                rule = IffRuleConverter(ruleDTO);
                break;
            case IMPLY:
                rule = ImplyRuleConverter(ruleDTO);
                break;
            case NUMERIC:
                rule = NumericRuleConverter(ruleDTO);
                break;
            case MULTIPLE_CHOICE:
                rule = MultipleChoiceRuleConverter(ruleDTO);
                break;
            default:
                rule = null;
        }

        return rule;
    }

    private Rule ANDRuleConverter(RuleDTO rule) {
        List<Rule> rules = new LinkedList<>();
        Rule currentRule;

        for(RuleDTO dto: rule.getSubRules())
        {
            currentRule = RuleConverter(dto);

            if(currentRule == null)
                return null;
            rules.add(currentRule);
        }

        return new AndRule(rules);
    }

    private OrRule OrRuleConverter(RuleDTO rule) {
        List<Rule> rules = new LinkedList<>();
        Rule currentRule;

        for(RuleDTO dto: rule.getSubRules())
        {
            currentRule = RuleConverter(dto);

            if(currentRule == null)
                return null;
            rules.add(currentRule);
        }


        return new OrRule(rules);
    }

    private IffRule IffRuleConverter(RuleDTO rule) {
        if(rule.getSubRules().size() != 2)
            return null;

        return new IffRule(RuleConverter(rule.getSubRules().get(0)), RuleConverter(rule.getSubRules().get(1)));
    }

    private IffRule ImplyRuleConverter(RuleDTO rule) {
        if(rule.getSubRules().size() != 2)
            return null;

        return new IffRule(RuleConverter(rule.getSubRules().get(0)), RuleConverter(rule.getSubRules().get(1)));
    }

    private MultipleChoiceBaseRule MultipleChoiceRuleConverter(RuleDTO rule) {
        return new MultipleChoiceBaseRule(rule.getQuestionID(), rule.getAnswer());
    }

    private NumericBaseRule NumericRuleConverter(RuleDTO rule) {
        return new NumericBaseRule(rule.getQuestionID(), rule.getComparison(), rule.getAnswer());
    }

}
