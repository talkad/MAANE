package Communication.Resource;

import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/User")
public class FixedUserController {
    private static final UserServiceImpl service = UserServiceImpl.getInstance();

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
