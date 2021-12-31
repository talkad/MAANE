//package Communication.Resource;
//
//import Communication.DTOs.EmptyUserDTO;
//import io.swagger.models.Model;
////import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//public class HomeController {
//
//    private Map<String, LocalDateTime> usersLastAccess = new HashMap<>();
//
//    @GetMapping("/")
//    public String getCurrentUser(@AuthenticationPrincipal EmptyUserDTO user, Model model) {
//        String username = user.getName();
//
////        model.addAttribute("username", username);
////        model.addAttribute("lastAccess", usersLastAccess.get(username));
//
//        usersLastAccess.put(username, LocalDateTime.now());
//
//        return "home";
//    }
//
//}
