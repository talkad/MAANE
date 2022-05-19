package Communication.Resource;

import Communication.Service.Interfaces.MonthlyReportGenerator;
import Domain.CommonClasses.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {

    private final MonthlyReportGenerator service;
    private final SessionHandler sessionHandler;


    @GetMapping("/getSurveys")
    public ResponseEntity<Response<byte[]>> getSurveys(@RequestHeader(value = "Authorization") String token){
        return ResponseEntity.ok()
                .body(service.generateMonthlyReport(sessionHandler.getUsernameByToken(token).getResult()));
    }



}
