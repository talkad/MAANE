package Communication.Resource;

import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Service.Interfaces.SurveyService;
import Service.SurveyServiceImpl;
import com.google.gson.Gson;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping("/survey")
public class SurveyController {

    private final SurveyService service = SurveyServiceImpl.getInstance();
    private Gson gson = new Gson();

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "text/plain")
    public ResponseEntity<Response<Integer>> createSurvey(@RequestBody String body){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        Properties p = gson.fromJson(body, Properties.class);
        SurveyDTO survey = new SurveyDTO(convertInteger((String)p.get("id")), (String)p.get("title"), (String)p.get("description"), null, null,
                 null);

        System.out.println( p.get("questions"));
        System.out.println(p.get("answers"));
        System.out.println(p.get("types"));

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.createSurvey((String)p.get("username"), survey));
    }

    @RequestMapping(value = "/submitAnswers", method = RequestMethod.POST, consumes = "text/plain")
    public ResponseEntity<Response<Boolean>> addAnswers(@RequestBody String body){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        Properties p = gson.fromJson(body, Properties.class);
        //         SurveyAnswersDTO answers = new SurveyAnswersDTO(convertInteger((String)p.get("id")), p.get("answers"), p.get("types"));
        SurveyAnswersDTO answers = null;//new SurveyAnswersDTO(convertInteger((String)p.get("id")), null, null);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.addAnswers(answers));
    }

    @GetMapping("/get/surveyID={surveyID}")
    public ResponseEntity<Response<SurveyDTO>> getSurvey(@PathVariable("surveyID") int surveyID){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.getSurvey(surveyID));
    }

    // TODO - define a rule
    @PostMapping("/addRule")
    public ResponseEntity<Response<Boolean>> addRule(@RequestBody Map<String, Object> body){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body( service.addRule((String)body.get("username"), (Integer)body.get("surveyID"), (Rule)body.get("rule"), (Integer)body.get("goalID")));
    }

    @GetMapping("/detectFault/username={username}&surveyID={surveyID}")
    public ResponseEntity<Response<List<List<String>>>> detectFault(@PathVariable("username") String username, @PathVariable("surveyID") int surveyID){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.detectFault(username, surveyID));
    }

    public int convertInteger(String num){
        try{
            return Integer.parseInt(num);
        }
        catch(Exception e){
            return -1;
        }
    }

}
