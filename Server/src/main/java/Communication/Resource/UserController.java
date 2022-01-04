package Communication.Resource;

import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Domain.UsersManagment.UserStateEnum;
import Service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
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



}
