package Communication.Resource;

import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Service.UserServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final UserServiceImpl service = UserServiceImpl.getInstance();

    @GetMapping("/startup")
    public ResponseEntity<Response<String>> startup(){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        return ResponseEntity.ok().headers(responseHeaders)
                .body(service.addGuest());
//        return ResponseEntity.ok(
//
//                service.addGuest()
//        ).headers;
    }

    @PostMapping("/removeGuest")
    public ResponseEntity<Response<String>> removeGuest(@RequestBody Map<String, String> body){
        return ResponseEntity.ok(
                service.removeGuest(body.get("name"))
        );
    }

    @PostMapping("/addGuest")
    public ResponseEntity<Response<String>> addGuest(){
        return ResponseEntity.ok(
                service.addGuest()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@RequestBody Map<String, String> body){
        return ResponseEntity.ok(
                service.login(body.get("currUser"), body.get("userToLogin"), body.get("password"))
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(@RequestBody Map<String, String> body){
        return ResponseEntity.ok(
                service.logout(body.get("name"))
        );
    }

    @PostMapping("/registerUser")
    public ResponseEntity<Response<User>> registerUser(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(
                service.registerUser(userDTO)
        );
    }

    @PostMapping("/removeUser")
    public ResponseEntity<Response<Boolean>> removeUser(@RequestBody Map<String, String> body){
        return ResponseEntity.ok(
                service.removeUser(body.get("currUser"), body.get("userToRemove"))
        );
    }
}
