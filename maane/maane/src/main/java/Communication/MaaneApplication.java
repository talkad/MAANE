package Communication;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.SurveyDTO;
import Communication.DTOs.UserDTO;
import Communication.Initializer.ServerContextInitializer;
import Communication.Service.UserServiceImpl;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.Rules.*;
import Domain.DataManagement.SurveyController;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.UserStateEnum;
import Domain.WorkPlan.GoalsManagement;
import Persistence.GoalsQueries;
import Persistence.SurveyDAO;
import Persistence.UserQueries;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

			if(ServerContextInitializer.getInstance().isMockMode()){
				System.out.println("Mock Mode is Activated!");

				UserQueries.getInstance().clearDB();

				UserController userController = UserController.getInstance();
				userController.login("admin");

				service.registerUserBySystemManager("admin", new UserDTO("admin", "tech", "tal", "1234", UserStateEnum.SUPERVISOR,
						"tal", "kad", "tal@gmail.com", "055-555-5555", "", null), "");
				userController.login("tal");

				userController.logout("admin");

				// create survey
				SurveyDTO surveyDTO = new SurveyDTO(false, "1111", "title", "description",
						Arrays.asList("symbol", "open?", "numeric?", "multiple choice?"),
						Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), Arrays.asList("correct", "wrong")),
						Arrays.asList(AnswerType.NUMERIC_ANSWER, AnswerType.OPEN_ANSWER, AnswerType.NUMERIC_ANSWER, AnswerType.MULTIPLE_CHOICE));

				SurveyController.getInstance().createSurvey("tal", surveyDTO);

				// create goals
				GoalDTO goalDTO1 = new GoalDTO(555, "yahad1", "", 1,
						5, "tech", 2022);
				GoalDTO goalDTO2 = new GoalDTO(666, "yahad2", "", 2,
						10, "tech",2022);

				GoalsManagement.getInstance().addGoalToField("tech", goalDTO1, 2022);
				GoalsManagement.getInstance().addGoalToField("tech", goalDTO2, 2022);

//				// create rules
//				Rule rule1 = new AndRule(Arrays.asList(new NumericBaseRule(2, Comparison.EQUAL, 30),
//														new MultipleChoiceBaseRule(3, List.of(1))));
//				Rule rule2 = new NumericBaseRule(2, Comparison.EQUAL, 30);
//
//				SurveyDAO.getInstance().insertRule("1111", 555, rule1.getDTO());
//				SurveyDAO.getInstance().insertRule("1111", 666, rule2.getDTO());
//
//				// add answers
//				SurveyDAO.getInstance().insertCoordinatorAnswers("1111", "01234",
//						new LinkedList<>(Arrays.asList("open ans", "20", "0")),
//						new LinkedList<>(Arrays.asList(AnswerType.OPEN_ANSWER, AnswerType.NUMERIC_ANSWER, AnswerType.MULTIPLE_CHOICE)));
//
//				SurveyDAO.getInstance().insertCoordinatorAnswers("1111", "56789",
//						new LinkedList<>(Arrays.asList("open ans", "40", "1")),
//						new LinkedList<>(Arrays.asList(AnswerType.OPEN_ANSWER, AnswerType.NUMERIC_ANSWER, AnswerType.MULTIPLE_CHOICE)));
//				// create another survey
//				surveyDTO = new SurveyDTO(true, "2222", "title", "description",
//						Arrays.asList("symbol", "open?", "numeric?", "multiple choice?"),
//						Arrays.asList(new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), Arrays.asList("correct", "wrong")),
//						Arrays.asList(AnswerType.NUMERIC_ANSWER, AnswerType.OPEN_ANSWER, AnswerType.NUMERIC_ANSWER, AnswerType.MULTIPLE_CHOICE));
//
//
//				SurveyDAO.getInstance().insertSurvey(surveyDTO);

				userController.logout("logout");

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
