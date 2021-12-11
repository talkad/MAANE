package Communication.Resource;

import Communication.Model.Instructor;
import Communication.Model.Response;
import Communication.Service.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/server")
@RequiredArgsConstructor
public class ServerController {

    private final ServiceImpl service;

    @GetMapping("/getAll")
    public ResponseEntity<Response> getAll(){
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("ins", service.list()))
                        .msg("get all")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Response> add(@RequestBody Instructor ins){
        service.add(ins);
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("ins", "added"))
                        .msg("add")
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

    @DeleteMapping("/delete/{idx}")
    public ResponseEntity<Response> delete(@PathVariable("idx") int idx){
        service.delete(idx);
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("ins", "delete"))
                        .msg("delete")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/get/{idx}")
    public ResponseEntity<Response> get(@PathVariable("idx") int idx){
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("ins", service.getIns(idx)))
                        .msg("delete")
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
