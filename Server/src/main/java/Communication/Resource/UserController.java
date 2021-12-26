package Communication.Resource;

import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserStateEnum;
import Domain.UsersManagment.User;
import Service.UserServiceImpl;
import com.google.gson.Gson;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Properties;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final UserServiceImpl service = UserServiceImpl.getInstance();
    private Gson gson = new Gson();

    @GetMapping("/startup")
    public ResponseEntity<Response<String>> startup(){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.addGuest());
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "text/plain")
    public ResponseEntity<Response<String>> login(@RequestBody String body){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        Properties p = gson.fromJson(body, Properties.class);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.login((String)p.get("currUser"),  (String)p.get("userToLogin"), (String)p.get("password")));
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST, consumes = "text/plain")
    public ResponseEntity<Response<String>> logout(@RequestBody String body){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        Properties p = gson.fromJson(body, Properties.class);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.logout((String)p.get("name")));
    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST, consumes = "text/plain")
    public ResponseEntity<Response<User>> registerUser(@RequestBody String body) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "*");

        Properties p = gson.fromJson(body, Properties.class);
        UserDTO user = new UserDTO((String)p.get("currUser"), (String)p.get("userToRegister"), (String)p.get("password"), enumState((String)p.get("userStateEnum")),
                (String)p.get("firstName"), (String)p.get("lastName"), (String)p.get("email"), (String)p.get("phoneNumber"), (String)p.get("city"));

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.registerUser(user));
    }

    @RequestMapping(value = "/removeUser", method = RequestMethod.POST, consumes = "text/plain")
    public ResponseEntity<Response<Boolean>> removeUser(@RequestBody String body){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "*");

        Properties p = gson.fromJson(body, Properties.class);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.removeUser((String)p.get("currUser"), (String)p.get("userToRemove")));

    }

    private UserStateEnum enumState(String userState){
        switch(userState){
            case "INSTRUCTOR":
                return UserStateEnum.INSTRUCTOR;
            case "SUPERVISOR":
                return UserStateEnum.SUPERVISOR;
            case "GENERAL_SUPERVISOR":
                return UserStateEnum.GENERAL_SUPERVISOR;
            case "SYSTEM_MANAGER":
                return UserStateEnum.SYSTEM_MANAGER;
            default:
                return UserStateEnum.GUEST;
        }
    }
}