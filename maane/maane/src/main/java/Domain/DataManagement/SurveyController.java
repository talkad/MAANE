package Domain.DataManagement;

import Communication.DTOs.*;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.FaultDetector;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Domain.DataManagement.FaultDetector.Rules.RuleConverter;
import Domain.UsersManagment.UserController;
import Persistence.SurveyDAO;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class SurveyController {

    private final SecureRandom secureRandom;
    private final Base64.Encoder base64Encoder;

    private final SurveyDAO surveyDAO;



    private static class CreateSafeThreadSingleton {
        private static final SurveyController INSTANCE = new SurveyController();
    }

    public static SurveyController getInstance() {
        return CreateSafeThreadSingleton.INSTANCE;
    }

    public SurveyController(){
        secureRandom = new SecureRandom();
        base64Encoder = Base64.getUrlEncoder();
        surveyDAO = SurveyDAO.getInstance();
    }

    /**
     * creates new survey
     * @param username the name of the user that wish to create a survey
     * @param surveyDTO is the questions and their types that the generated survey will contain
     * @return response with result of the new surveyID on success, -1 on failure
     */
    public Response<String> createSurvey(String username, SurveyDTO surveyDTO){
        Response<String> permissionRes;
        Response<Survey> surveyRes;

        String indexer = createToken();

        surveyDTO.setId(indexer);
        permissionRes = UserController.getInstance().createSurvey(username, indexer);

        if(permissionRes.isFailure() || permissionRes.getResult().length() == 0)
            return new Response<>("", true, permissionRes.getErrMsg());
        surveyRes = Survey.createSurvey(indexer, surveyDTO);

        if(surveyRes.isFailure()) {
            Response<String> res = UserController.getInstance().removeSurvey(username, indexer);

            if(res.isFailure())
                return res;

            return new Response<>("", true, surveyRes.getErrMsg());
        }

        surveyDAO.insertSurvey(surveyDTO);

        UserController.getInstance().notifySurveyCreation(username, indexer);

        return new Response<>(indexer, false, "new survey created successfully");
    }

    public Response<Boolean> addQuestion(String username, QuestionDTO questionDTO) {
        Response<Boolean> resDB;
        Response<SurveyDTO> resSurvey;
        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, questionDTO.getSurveyID());

        if(!legalAdd.getResult())
            return new Response<>(false, true, username + " does not created survey " + questionDTO.getSurveyID());

        resSurvey = surveyDAO.getSurvey(questionDTO.getSurveyID());

        if(resSurvey.isFailure())
            return new Response<>(false, true, resSurvey.getErrMsg());

        resDB = surveyDAO.addQuestion(questionDTO, resSurvey.getResult().getQuestions().size());

        if(resDB.isFailure())
            return new Response<>(false, true, resDB.getErrMsg());

        return new Response<>(true, false, "question added successfully");
    }

    public Response<Boolean> removeQuestion(String username, String surveyID, Integer questionID) {
        Response<Boolean> resDB;
        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, surveyID);

        if(!legalAdd.getResult())
            return new Response<>(false, true, username + " does not created survey " + surveyID);

        resDB = surveyDAO.removeQuestions(surveyID, questionID);

        if(resDB.isFailure())
            return new Response<>(false, true, resDB.getErrMsg());

        return new Response<>(true, false, "question removed successfully");

    }

    /**
     * new answers for certain survey
     * @param answersDTO is the answers for a given survey
     * @return response with boolean that represents if the answers was
     */
    public Response<Boolean> addAnswers(SurveyAnswersDTO answersDTO){

        SurveyAnswers answer = new SurveyAnswers();
        Response<Boolean> answerRes = answer.addAnswers(answersDTO);
        String symbol;

        if(answerRes.isFailure())
            return new Response<>(false, true, answerRes.getErrMsg());

        Response<Survey> surveyResponse = loadSurvey(answersDTO.getId());
        if(surveyResponse.isFailure())
            return new Response<>(false, true, surveyResponse.getErrMsg());

        if(surveyResponse.getResult().getQuestions().size() != answersDTO.getTypes().size())
            return new Response<>(false, true, "number of answers cannot be different from number of questions");

        if(answersDTO.getAnswers().size() == 0)
            return new Response<>(true, false, "empty survey");

        symbol = answersDTO.getAnswers().get(0);

        if(symbol.length() == 0)
            return new Response<>(false, true, "School symbol cannot be empty string");

        answer.setSymbol(symbol);
        List<String> answers = answersDTO.getAnswers();
        answers.remove(0);

        List<AnswerType> types = answersDTO.getTypes();
        types.remove(0);

        surveyDAO.insertCoordinatorAnswers(answersDTO.getId(), symbol, answers, types);

        return new Response<>(true, false, "the answer added successfully");
    }

    /**
     * get the survey by its ID
     * @param id of the desired survey
     * @return the survey with the given ID if exists, failure response otherwise
     */
    public Response<SurveyDTO> getSurvey(String id){
            return surveyDAO.getSurvey(id);
    }

    /**
     * add a new rule to a survey
     * @param username the name of the user that desired to add a rule
     * @param id of the survey
     * @param rule the new rule to be added
     * @param goalID the goal the rule represents
     * @return success response if the arguments are legal. failure otherwise
     */
    public Response<Boolean> addRule(String username, String id, Rule rule, int goalID){
        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, id);

        if(!legalAdd.getResult())
            return new Response<>(false, true, username + " does not created survey " + id);

        surveyDAO.insertRule(id, goalID, rule.getDTO());

        return new Response<>(true, false, "OK");
    }

    /**
     * remove a rule from a survey
     * @param username the name of the user desired to remove a rule
     * @param id of the survey
     * @param ruleID id of the rule to be removed
     * @return successful response if the {@username} created the survey in first place
     */
    public Response<Boolean> removeRule(String username, String id, int ruleID){
        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, id);

        if(!legalAdd.getResult())
            return new Response<>(false, true, username + " does not created survey " + id);

        return surveyDAO.removeRule(ruleID);
    }

    /**
     * remove all rules related to given surveyID
     * @param username the user trying to remove the rules
     * @param surveyID the survey all the rules will be deleted
     * @return if the function call succeeded
     */
    public Response<Boolean> removeRules(String username, String surveyID) {
        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, surveyID);

        if(!legalAdd.getResult())
            return new Response<>(false, true, username + " removed all rules from survey " + surveyID);

        return surveyDAO.removeRules(surveyID);
    }

    /**
     * detects irregularities in survey answers
     * @param username the name of the user want to detect fault
     * @param id of the survey
     * @return response contains list of all goals that not consistent with the rules, for each answer
     */
    public Response<List<List<String>>> detectFault(String username, String id, String year){

        List<List<String>> faults = new LinkedList<>();
        FaultDetector faultDetector;
        List<GoalDTO> goals = UserController.getInstance().getGoals(username, year).getResult();
        List<String> currentFaults;
        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, id);

        if(!legalAdd.getResult())
            return new Response<>(null, true, username + " does not created survey " + id);

        Response<Survey> surveyResponse = loadSurvey(id);
        if(surveyResponse.isFailure())
            return new Response<>(null, true, surveyResponse.getErrMsg());

        faultDetector = new FaultDetector(rulesConverter(surveyDAO.getRules(id)));
        List<SurveyAnswers> answers = answerConverter(surveyDAO.getAnswers(id));

        for(SurveyAnswers ans: answers){
            currentFaults = new LinkedList<>();

            for(Integer fault: faultDetector.detectFault(ans).getResult())
                currentFaults.add(goals.get(fault).getTitle());

            faults.add(currentFaults);
        }

        return new Response<>(faults, false, "faults detected");
    }

//    public Map<String, List<SurveyAnswers>> getAnswers() {
//        Map<String, List<SurveyAnswers>> answers = new ConcurrentHashMap<>();
//        Map<String, List<SurveyAnswersDTO>> answersDB = surveyDAO.getAllAnswers();
//
//        for(String key: answersDB.keySet())
//            answers.put(key, answerConverter(answersDB.get(key)));
//
//        return answers;
//    }

    public Response<List<SurveyAnswers>> getAnswersForSurvey(String surveyId) {
        return new Response<>(answerConverter(surveyDAO.getAnswers(surveyId)), false, "OK");
    }

    public Response<List<Rule>> getRules(String surveyID){
        FaultDetector fd;
        List<Rule> rules = new LinkedList<>();

        fd = new FaultDetector(rulesConverter(surveyDAO.getRules(surveyID)));
        for(Pair<Rule, Integer> p: fd.getRules()){
            rules.add(p.getFirst());
        }

        return new Response<>(rules, false, "success");
    }

    public Response<List<SurveyDetailsDTO>> getSurveys(String username){
        Response<List<String>> res = UserController.getInstance().getSurveys(username);
        List<SurveyDetailsDTO> surveyInfo = new LinkedList<>();
        Response<SurveyDTO> survey;
        StringBuilder errMsg = new StringBuilder("couldn't load surveys: \n");

        if(res.isFailure())
            return new Response<>(new LinkedList<>(), true, res.getErrMsg());

        if(res.getResult() == null)
            return new Response<>(new LinkedList<>(), false, "There are no surveys");

        for(String surveyID: res.getResult()) {
            survey = surveyDAO.getSurvey(surveyID);

            if(!survey.isFailure())
                surveyInfo.add(new SurveyDetailsDTO(survey.getResult().getTitle(), survey.getResult().getDescription(), surveyID));
            else
                errMsg.append(surveyID).append("\n");
        }

        return new Response<>(surveyInfo, false, errMsg.toString());
    }


    /**
     * detect all irregularities in certain school
     * @param username the name of the user that want to detect the faults
     * @param id of the survey
     * @param symbol of the school
     * @return list of all goals that not consistent with the rules
     */
    public Response<List<Integer>> detectSchoolFault(String username, String id, String symbol, String year){
        FaultDetector faultDetector;
        List<GoalDTO> goals = UserController.getInstance().getGoals(username, year).getResult();
        List<Integer> currentFaults = new LinkedList<>();

        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, id);

        if(!legalAdd.getResult())
            return new Response<>(null, true, username + " did not create survey " + id);

        faultDetector = new FaultDetector(rulesConverter(surveyDAO.getRules(id)));
        List<SurveyAnswers> answers = answerConverter(surveyDAO.getAnswers(id));

        for(SurveyAnswers ans: answers){

            if(ans.getSymbol().equals(symbol)){
                for(Integer fault: faultDetector.detectFault(ans).getResult())
                    currentFaults.add(goals.get(fault).getGoalId());
            }
        }

        return new Response<>(currentFaults, false, "faults detected");
    }

    public Response<List<Integer>> detectSchoolFaultsMock(List<Pair<String, List<Integer>>> schoolsAndFaults, String schoolId){
        for (Pair<String, List<Integer>> schoolAndFaults: schoolsAndFaults)
        {
            if(schoolId.equals(schoolAndFaults.getFirst())) {
                //System.out.println("schoodId: " + schoolId + " schoolidFromList: " + schoolAndFaults.getFirst() + " faults: " + schoolAndFaults.getSecond().toString());
                return new Response<>(schoolAndFaults.getSecond(), false, "faults detected");
            }
        }
        return new Response<>(null, false, "faults detected");
    }

    private String createToken(){
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }



    private List<Pair<Rule, Integer>> rulesConverter(List<Pair<RuleDTO, Integer>> ruleDTOs){
        List<Pair<Rule, Integer>> rules = new LinkedList<>();

        for(Pair<RuleDTO, Integer> rule: ruleDTOs){
            rules.add(new Pair<>(RuleConverter.getInstance().convertRule(rule.getFirst()), rule.getSecond()));
        }

        return rules;
    }

    private List<SurveyAnswers> answerConverter(List<SurveyAnswersDTO> answerDTOs){
        List<SurveyAnswers> answers = new LinkedList<>();

        for(SurveyAnswersDTO ans: answerDTOs){
            answers.add(new SurveyAnswers(ans));
        }

        return answers;
    }

    private Response<Survey> loadSurvey(String id){

        Survey survey;
        Response<SurveyDTO> surveyRes;

        surveyRes = surveyDAO.getSurvey(id);

        if(surveyRes.isFailure())
            return new Response<>(null, true, surveyRes.getErrMsg());

        Response<Survey> surveyCreation = Survey.createSurvey(surveyRes.getResult().getId(), surveyRes.getResult());

        if(surveyCreation.isFailure())
            return new Response<>(null, true, surveyCreation.getErrMsg());

        survey = surveyCreation.getResult();


        return new Response<>(survey, false, "OK");
    }

}
