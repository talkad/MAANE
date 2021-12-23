package Communication.Resource;

import Communication.ConnectionManager;
import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Service.UserServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final ConnectionManager connectionManager = ConnectionManager.getInstance();
    private static final UserServiceImpl service = UserServiceImpl.getInstance();

    @GetMapping("/startup")
    public ResponseEntity<Response<String>> startup(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Response<String> res = service.addGuest();
        connectionManager.addNewConnection(auth.getPrincipal(), res.getResult());

        System.out.println("----------------" + auth.getPrincipal());

        return ResponseEntity.ok(
                res
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@RequestBody Map<String, String> body){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Response<String> res = service.login(body.get("currUser"), body.get("userToLogin"), body.get("password"));
        Response<Boolean> resUsername = connectionManager.setUsername(auth.getPrincipal(), res.getResult());

        if(resUsername.isFailure())
            System.out.println("----------------" + resUsername.getErrMsg());

        return ResponseEntity.ok(
                res
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(@RequestBody Map<String, String> body){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Response<String> res = service.logout(body.get("name"));
        Response<Boolean> resUsername = connectionManager.removeConnection(auth.getPrincipal());

        if(resUsername.isFailure())
            System.out.println("----------------" + resUsername.getErrMsg());

        return ResponseEntity.ok(
                res
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

    // TODO- remove this functions?

    //    @PostMapping("/removeGuest")
//    public ResponseEntity<Response<String>> removeGuest(@RequestBody Map<String, String> body){
//        return ResponseEntity.ok(
//                service.removeGuest(body.get("name"))
//        );
//    }

//    @PostMapping("/addGuest")
//    public ResponseEntity<Response<String>> addGuest(){
//        return ResponseEntity.ok(
//                service.addGuest()
//        );
//    }
}
