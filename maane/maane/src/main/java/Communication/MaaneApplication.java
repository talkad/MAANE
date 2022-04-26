package Communication;

import Communication.DTOs.UserDTO;
import Communication.Initializer.ServerContextInitializer;
import Communication.Security.KeyLoader;
import Communication.Service.UserServiceImpl;
import DataManagement.DataController;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Persistence.UserQueries;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@SpringBootApplication
public class MaaneApplication {

	public static void main(String[] args) {

		ServerContextInitializer.getInstance().setMockMode();

		SpringApplication.run(MaaneApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

	@Bean
	public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
	}

	@Bean
	CommandLineRunner run(UserServiceImpl service){
		return args -> {
			UserQueries.getInstance().deleteUsers();
			UserController userController = UserController.getInstance();
			userController.login("admin");

			service.registerUserBySystemManager("admin", new UserDTO("admin", "tech", "tal", "1234", UserStateEnum.SUPERVISOR,
					"tal", "kad", "", "", "", null), "");

			userController.logout("admin");

			if(ServerContextInitializer.getInstance().isMockMode()){
				UserQueries.getInstance().clearDB();
				System.out.println("Mock Mode is Activated!");
			}

		};
	}

//    @Bean
//    public EmbeddedServletContainerFactory createServletContainer() {
//        TomcatEmbeddedServletContainerFactory tomcatContainer = new TomcatEmbeddedServletContainerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint constraint = new SecurityConstraint();
//                constraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection securityCollection = new SecurityCollection();
//                securityCollection.addPattern("/*");
//                constraint.addCollection(securityCollection);
//                context.addConstraint(constraint);
//            }
//        };
//        tomcatContainer.addAdditionalTomcatConnectors(redirectConnectorHttp());
//        return tomcatContainer;
//    }
//
//    private Connector redirectConnectorHttp() {
//        Connector redirectConnector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        redirectConnector.setScheme("http");
//        redirectConnector.setPort(8080);
//        redirectConnector.setSecure(false);
//        redirectConnector.setRedirectPort(8443);
//        return redirectConnector;
//    }

}
