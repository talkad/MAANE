package Communication.Resource;

import Communication.DTOs.LoginDTO;
import Communication.DTOs.UserDTO;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.User;
import Service.UserServiceImpl;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.addGuest());
    }

//    @PostMapping("/login")
    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "text/plain")
    public ResponseEntity<Response<String>> login(@RequestBody String currUser){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        System.out.println("hello there");
        System.out.println(currUser);
        System.out.println("general kenobi");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.login("a", "a", "a"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(@RequestBody Map<String, String> body){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.logout(body.get("name")));
    }

    @PostMapping("/registerUser")
    public ResponseEntity<Response<User>> registerUser(@RequestBody UserDTO userDTO) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "*");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.registerUser(userDTO));
    }

    public ResponseEntity<Response<Boolean>> removeUser(@RequestBody Map<String, String> body){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin", "*");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(service.removeUser(body.get("currUser"), body.get("userToRemove")));

    }

}
