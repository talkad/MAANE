package Communication.DTOs.Resource;

import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserStateEnum;
import Service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/User")
public class FixedUserController {
    private static final UserServiceImpl service = UserServiceImpl.getInstance();

    @PostMapping("/removeGuest")
    public ResponseEntity<Response<String>> removeGuest(@PathVariable("name") String name){
        return ResponseEntity.ok(
                service.removeGuest(name)
        );
    }

    @PostMapping("/addGuest")
    public ResponseEntity<Response<String>> addGuest(){
        return ResponseEntity.ok(
                service.addGuest()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@PathVariable("currUser") String currUser, @PathVariable("userToLogin") String userToLogin, @PathVariable("password") String password){
        return ResponseEntity.ok(
                service.login(currUser, userToLogin, password)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(@PathVariable("name") String name){
        return ResponseEntity.ok(
                service.logout(name)
        );
    }

    //todo: can i use UserStateEnum here ?
    @PostMapping("/registerUser")
    public ResponseEntity<Response<Boolean>> registerUser(@PathVariable("currUser") String currUser, @PathVariable("userToRegister") String userToRegister, @PathVariable("password") String password, @PathVariable("userStateEnum") UserStateEnum userStateEnum){
        return ResponseEntity.ok(
                service.registerUser(currUser, userToRegister, password, userStateEnum)
        );
    }

    @PostMapping("/removeUser")
    public ResponseEntity<Response<Boolean>> removeUser(@PathVariable("currUser") String currUser, @PathVariable("userToRemove") String userToRemove){
        return ResponseEntity.ok(
                service.removeUser(currUser, userToRemove)
        );
    }
}
