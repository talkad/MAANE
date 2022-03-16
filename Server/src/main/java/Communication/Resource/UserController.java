package Communication.Resource;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.Goal;
import Service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    ObjectMapper objectMapper = new ObjectMapper();
    private Gson gson = new Gson();
    private static final UserServiceImpl service = UserServiceImpl.getInstance();

    @GetMapping("/startup")
    public ResponseEntity<Response<String>> startup(){
        return ResponseEntity.ok()
                .body(service.addGuest());
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Response<Pair<String, UserStateEnum>>> login(@RequestBody Map<String, Object> body){
        return ResponseEntity.ok()
                .body(service.login((String)body.get("currUser"), (String)body.get("userToLogin"), (String)body.get("password")));
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<Response<String>> logout(@RequestBody Map<String, Object>  body){
        return ResponseEntity.ok()
                .body(service.logout((String)body.get("name")));
    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public ResponseEntity<Response<User>> registerUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok()
                .body(service.registerUser(user));
    }

    @RequestMapping(value = "/removeUser", method = RequestMethod.POST)
    public ResponseEntity<Response<Boolean>> removeUser(@RequestBody Map<String, Object>  body){
        return ResponseEntity.ok()
                .body(service.removeUser((String)body.get("currUser"), (String)body.get("userToRemove")));
    }

    @RequestMapping(value = "/viewWorkPlan", method = RequestMethod.POST)
    public ResponseEntity<Response<WorkPlanDTO>> viewWorkPlan(@RequestBody Map<String, Object>  body){
        return ResponseEntity.ok()
                .body(service.viewWorkPlan((String)body.get("username"), (String)body.get("year")));
    }

    @RequestMapping(value = "/authenticatePassword", method = RequestMethod.POST)
    public ResponseEntity<Response<Boolean>> verifyUser(@RequestBody Map<String, Object>  body){
        return ResponseEntity.ok()
                .body(service.verifyUser((String)body.get("currUser"), (String)body.get("password")));
    }

///*    @GetMapping("/getAppointedUsers/username={currUser}")
//        public ResponseEntity<Response<List<UserDTO>>> getAppointedUsers(@PathVariable("currUser") String currUser){
//            return ResponseEntity.ok()
//                    .body(service.getAppointedUsers(currUser));
//        }*/

    @RequestMapping(value = "/getAppointedUsers", method = RequestMethod.POST)
    public ResponseEntity<Response<List<UserDTO>>> getAppointedUsers(@RequestBody Map<String, Object>  body){
        return ResponseEntity.ok()
                .body(service.getAppointedUsers((String)body.get("currUser")));
    }

//    @RequestMapping(value = "/removeUser", method = RequestMethod.POST)
//    public ResponseEntity<Response<Boolean>> generateSchedule(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.generateSchedule((String)body.get("supervisor"), (Integer) body.get("surveyID")));
//    }

    @RequestMapping(value = "/addGoal", method = RequestMethod.POST)
    public ResponseEntity<Response<Boolean>> addGoal(@RequestBody Map<String, Object>  body){
        String goal = "";

        try {
            goal = objectMapper.writeValueAsString(body.get("goalDTO"));
        }catch(Exception e){
            System.out.println("This exception shouldn't occur");
        }

        return ResponseEntity.ok()
                .body(service.addGoal((String)body.get("currUser"), gson.fromJson(goal, GoalDTO.class), (String)body.get("year")));
    }

    @RequestMapping(value = "/removeGoal", method = RequestMethod.POST)
    public ResponseEntity<Response<Boolean>> removeGoal(@RequestBody Map<String, Object>  body){
        return ResponseEntity.ok()
                .body(service.removeGoal((String)body.get("currUser"), (String)body.get("year"), (int)body.get("goalId")));
    }

    @RequestMapping(value = "/getGoals", method = RequestMethod.POST)
    public ResponseEntity<Response<List<GoalDTO>>> getGoals(@RequestBody Map<String, Object>  body){
        return ResponseEntity.ok()
                .body(service.getGoals((String)body.get("currUser"), (String)body.get("year")));
    }
}
