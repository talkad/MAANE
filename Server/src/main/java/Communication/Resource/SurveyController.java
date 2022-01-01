package Communication.Resource;

import Communication.DTOs.RuleDTO;
import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.*;
import Service.Interfaces.SurveyService;
import Service.SurveyServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
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

    @RequestMapping(value ="/addRule",  method = RequestMethod.POST)
    public ResponseEntity<Response<Boolean>> addRule(@RequestBody Map<String, Object> body){
        Rule rule = RuleConverter((RuleDTO)body.get("rule"));

        if(rule == null)
            return ResponseEntity.ok()
                    .body(new Response<>(false, true, "rule converter failed"));
        return ResponseEntity.ok()
                .body( service.addRule((String)body.get("username"), (Integer)body.get("surveyID"), rule, (Integer)body.get("goalID")));
    }

    @GetMapping("/detectFault/username={username}&surveyID={surveyID}")
    public ResponseEntity<Response<List<List<String>>>> detectFault(@PathVariable("username") String username, @PathVariable("surveyID") int surveyID){
        return ResponseEntity.ok()
                .body(service.detectFault(username, surveyID));
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
