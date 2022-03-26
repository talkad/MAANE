package Communication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MaaneApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaaneApplication.class, args);
	}

//	@Bean
//	PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }

//	CommandLineRunner run(UserService service){
//		return args -> {
//			service.registerUser(new UserDTO());
//		};
//	}

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
