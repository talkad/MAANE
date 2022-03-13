package Communication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// (exclude = { SecurityAutoConfiguration.class })
public class ServerSecuredApplication {

    public static void main(String[] args){
//        SpringApplication.run(ServerApplication.class, args);

        SpringApplication application = new SpringApplication(ServerSecuredApplication.class);
        application.setAdditionalProfiles("ssl");
        application.run(args);
    }

}
