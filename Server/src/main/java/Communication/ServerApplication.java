package Communication;

import Communication.Model.Instructor;
import Communication.Repo.Repo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args){
        SpringApplication.run(ServerApplication.class, args);
    }

//    @Bean
//    CommandLineRunner run(Repo repo){
//        return args -> {
//          repo.add(new Instructor("tal@post", "tal"));
//        };
//    }
}
