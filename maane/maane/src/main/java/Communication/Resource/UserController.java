package Communication.Resource;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.SchoolManagementDTO;
import Communication.DTOs.UserDTO;
import Communication.DTOs.WorkPlanDTO;
import Communication.UserPersistency.Entity.UserInfo;
import Communication.UserPersistency.Service.UserInfoService;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserStateEnum;
import Service.UserServiceImpl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
//@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

//    ObjectMapper objectMapper = new ObjectMapper();
//    private final Gson gson = new Gson();
//    private static final UserServiceImpl service = UserServiceImpl.getInstance();

    private final UserInfoService userInfoService;

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){

            try{
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8)); // todo: decrypt and encrypt this key in production
                JWTVerifier verifier = JWT.require(algorithm).build();

                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();

                UserInfo userInfo = userInfoService.getUser(username);
                List<String> authorities = new LinkedList<>();
                authorities.add(userInfo.getRole());

                String accessToken = JWT.create()
                        .withSubject(userInfo.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", authorities)
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            }catch(Exception e){

                response.setHeader("error", e.getMessage());
                response.setStatus(FORBIDDEN.value());

                Map<String, String> error = new HashMap<>();
                error.put("error_message",  e.getMessage());

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
        else {
            throw new RuntimeException("Refresh token is missing");
        }

    }

    @GetMapping("/test")
    public ResponseEntity<String> testSSL(){
        return ResponseEntity.ok().body("hello world");
    }

    @GetMapping("/test2")
    public ResponseEntity<UserInfo> test2(){
        return ResponseEntity.ok().body(userInfoService.getUser("tal"));
    }

//    @GetMapping("/startup")
//    public ResponseEntity<Response<String>> startup(){
//        return ResponseEntity.ok()
//                .body(service.addGuest());
//    }
//
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    public ResponseEntity<Response<Pair<String, UserStateEnum>>> login(@RequestBody Map<String, Object> body){
//        return ResponseEntity.ok()
//                .body(service.login((String)body.get("currUser"), (String)body.get("userToLogin"), (String)body.get("password")));
//    }
//
//    @RequestMapping(value = "/logout", method = RequestMethod.POST)
//    public ResponseEntity<Response<String>> logout(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.logout((String)body.get("name")));
//    }
//
//    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)//todo aviad
//    public ResponseEntity<Response<String>> registerUser(@RequestBody UserDTO user) {
//        return ResponseEntity.ok()
//                .body(service.registerUser(user));
//    }
//
//    @RequestMapping(value = "/registerUserBySystemManager", method = RequestMethod.POST)//todo aviad
//    public ResponseEntity<Response<String>> registerUserBySystemManager(@RequestBody Map<String, Object>  body) {
//        return ResponseEntity.ok()
//                .body(service.registerUserBySystemManager((UserDTO) body.get("user"), (String)body.get("optionalSupervisor")));
//    }
//
///*    @RequestMapping(value = "/registerUserBySystemManager", method = RequestMethod.POST)
//    public ResponseEntity<Response<User>> registerUserBySystemManager(@RequestBody UserDTO user) {
//        return ResponseEntity.ok()
//                .body(service.registerUserBySystemManager(user));
//    }*/ //todo fix dto so it adds opt sup
//
//    @RequestMapping(value = "/removeUser", method = RequestMethod.POST)
//    public ResponseEntity<Response<Boolean>> removeUser(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.removeUser((String)body.get("currUser"), (String)body.get("userToRemove")));
//    }
//
//    @RequestMapping(value = "/viewWorkPlan", method = RequestMethod.POST)
//    public ResponseEntity<Response<WorkPlanDTO>> viewWorkPlan(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.viewWorkPlan((String)body.get("username"), (String)body.get("year")));
//    }
//
//    @RequestMapping(value = "/authenticatePassword", method = RequestMethod.POST)
//    public ResponseEntity<Response<Boolean>> verifyUser(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.verifyUser((String)body.get("currUser"), (String)body.get("password")));
//    }
//
/////*    @GetMapping("/getAppointedUsers/username={currUser}")
////        public ResponseEntity<Response<List<UserDTO>>> getAppointedUsers(@PathVariable("currUser") String currUser){
////            return ResponseEntity.ok()
////                    .body(service.getAppointedUsers(currUser));
////        }*/
//
//    @RequestMapping(value = "/getAppointedUsers", method = RequestMethod.POST)
//    public ResponseEntity<Response<List<UserDTO>>> getAppointedUsers(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.getAppointedUsers((String)body.get("currUser")));
//    }
//
////    @RequestMapping(value = "/generateSchedule", method = RequestMethod.POST)
////    public ResponseEntity<Response<Boolean>> generateSchedule(@RequestBody Map<String, Object>  body){
////        return ResponseEntity.ok()
////                .body(service.generateSchedule((String)body.get("supervisor"), (Integer) body.get("surveyID")));
////    }
//
//    @RequestMapping(value = "/addGoal", method = RequestMethod.POST)
//    public ResponseEntity<Response<Boolean>> addGoal(@RequestBody Map<String, Object>  body){
//        String goal = "";
//
//        try {
//            goal = objectMapper.writeValueAsString(body.get("goalDTO"));
//        }catch(Exception e){
//            System.out.println("This exception shouldn't occur");
//        }
//
//        return ResponseEntity.ok()
//                .body(service.addGoal((String)body.get("currUser"), gson.fromJson(goal, GoalDTO.class), (String)body.get("year")));
//    }
//
//    @RequestMapping(value = "/removeGoal", method = RequestMethod.POST)
//    public ResponseEntity<Response<Boolean>> removeGoal(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.removeGoal((String)body.get("currUser"), (String)body.get("year"), (int)body.get("goalId")));
//    }
//
//    @RequestMapping(value = "/getGoals", method = RequestMethod.POST)
//    public ResponseEntity<Response<List<GoalDTO>>> getGoals(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.getGoals((String)body.get("currUser"), (String)body.get("year")));
//    }
//
//   @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
//    public ResponseEntity<Response<Boolean>> updateInfo(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.updateInfo((String)body.get("currUser"), (String)body.get("firstName"), (String)body.get("lastName"), (String)body.get("email"), (String)body.get("phoneNumber"), (String)body.get("city")));
//    }
//
//    @RequestMapping(value = "/changePasswordToUser", method = RequestMethod.POST)
//    public ResponseEntity<Response<Boolean>> changePasswordToUser(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.changePasswordToUser((String)body.get("currUser"), (String)body.get("userToChangePassword"), (String)body.get("newPassword"), (String)body.get("confirmPassword")));
//    }
//
//    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
//    public ResponseEntity<Response<Boolean>> changePassword(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.changePassword((String)body.get("currUser"), (String)body.get("currPassword"), (String)body.get("newPassword"), (String)body.get("confirmPassword")));
//    }
//
//    @RequestMapping(value = "/getAllUsers", method = RequestMethod.POST)//todo aviad
//    public ResponseEntity<Response<List<UserDTO>>> getAllUsers(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.getAllUsers((String)body.get("currUser")));
//    }
//
//    @RequestMapping(value = "/assignSchoolsToUser", method = RequestMethod.POST)//todo aviad
//    public ResponseEntity<Response<Boolean>> assignSchoolsToUser(@RequestBody SchoolManagementDTO schoolManagementDTO){
//        return ResponseEntity.ok()
//                .body(service.assignSchoolsToUser(schoolManagementDTO.getCurrUser(), schoolManagementDTO.getAffectedUser(), schoolManagementDTO.getSchools()));
//    }
//
//    @RequestMapping(value = "/removeSchoolsFromUser", method = RequestMethod.POST)//todo aviad
//    public ResponseEntity<Response<Boolean>> removeSchoolsFromUser(@RequestBody SchoolManagementDTO schoolManagementDTO){
//        return ResponseEntity.ok()
//                .body(service.removeSchoolsFromUser(schoolManagementDTO.getCurrUser(), schoolManagementDTO.getAffectedUser(), schoolManagementDTO.getSchools()));
//    }
//
//    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
//    public ResponseEntity<Response<UserDTO>> getUserInfo(@RequestBody Map<String, Object>  body){
//        return ResponseEntity.ok()
//                .body(service.getUserInfo((String)body.get("currUser")));
//    }
}
