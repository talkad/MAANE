package Communication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// (exclude = { SecurityAutoConfiguration.class })
public class ServerSecuredApplication {

    public static void main(String[] args){
        SpringApplication.run(ServerSecuredApplication.class, args);

//        SpringApplication application = new SpringApplication(ServerSecuredApplication.class);
//        application.setAdditionalProfiles("ssl");
//        application.run(args);
    }

}
