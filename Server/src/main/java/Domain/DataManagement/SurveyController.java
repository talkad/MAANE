package Domain.DataManagement;

import Communication.DTOs.GoalDTO;
import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.FaultDetector;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Domain.WorkPlan.Goal;
import Domain.UsersManagment.UserController;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SurveyController {
    private Map<Integer, Pair<Survey, FaultDetector>> surveys;
    private Map<Integer, List<SurveyAnswers>> answers;
    private int indexer;

    private static class CreateSafeThreadSingleton {
        private static final SurveyController INSTANCE = new SurveyController();
    }

    public static SurveyController getInstance() {
        return SurveyController.CreateSafeThreadSingleton.INSTANCE;
    }

    public SurveyController(){
        answers = new ConcurrentHashMap<>();
        surveys = new ConcurrentHashMap<>();
        indexer = 0;
    }

    /**
     * creates new survey
     * @param username the name of the user that wish to create a survey
     * @param surveyDTO is the questions and their types that the generated survey will contain
     * @return response with result of the new surveyID on success, -1 on failure
     */
    public Response<Integer> createSurvey(String username, SurveyDTO surveyDTO){
        Response<Integer> permissionRes;
        Response<Survey> surveyRes;

        permissionRes = UserController.getInstance().createSurvey(username, indexer);

        if(permissionRes.getResult() < 0)
            return new Response<>(-1, true, permissionRes.getErrMsg());

        surveyRes = Survey.createSurvey(indexer, surveyDTO);

        if(surveyRes.isFailure()) {
            Response<Integer> res = UserController.getInstance().removeSurvey(username, indexer);

            if(res.isFailure())
                return res;

            return new Response<>(-1, true, surveyRes.getErrMsg());
        }

        surveys.put(indexer, new Pair<>(surveyRes.getResult(), new FaultDetector()));

        UserController.getInstance().notifySurveyCreation(username, indexer);

        return new Response<>(indexer++, false, "new survey created successfully");
    }

    /**
     * new answers for certain survey
     * @param answersDTO is the answers for a given survey
     * @return response with boolean that represents if the answers was
     */
    public Response<Boolean> addAnswers(SurveyAnswersDTO answersDTO){

        SurveyAnswers answer = new SurveyAnswers();
        Response<Boolean> answerRes = answer.addAnswers(answersDTO);

        if(answerRes.isFailure())
            return new Response<>(false, true, answerRes.getErrMsg());

        if(!surveys.containsKey(answersDTO.getId()))
            return new Response<>(false, true, "cannot answer to a not exists survey");

        if(surveys.get(answersDTO.getId()).getFirst().getQuestions().size() != answersDTO.getTypes().size())
            return new Response<>(false, true, "number of answers cannot be different from number of questions");

        if(answersDTO.getSymbol().length() == 0)
            return new Response<>(false, true, "School symbol cannot be empty string");

        answer.setSymbol(answersDTO.getSymbol());

        if(!answers.containsKey(answersDTO.getId()))
            answers.put(answersDTO.getId(), new LinkedList<>());

        List<SurveyAnswers> ans = answers.get(answersDTO.getId());
        ans.add(answer);
        answers.put(answersDTO.getId(), ans);

        return new Response<>(true, false, "the answer added successfully");
    }

    /**
     * get the survey by its ID
     * @param id of the desired survey
     * @return the survey with the given ID if exists, failure response otherwise
     */
    public Response<SurveyDTO> getSurvey(int id){
        Survey survey;
        SurveyDTO surveyDTO = new SurveyDTO();
        List<String> questions = new LinkedList<>();
        List<List<String>> ans = new LinkedList<>();
        List<AnswerType> types = new LinkedList<>();

        if(!surveys.containsKey(id))
            return new Response<>(surveyDTO, true, "id is out of bound");

        survey = surveys.get(id).getFirst();

        surveyDTO.setId(id);
        surveyDTO.setTitle(survey.getTitle());
        surveyDTO.setDescription(survey.getDescription());

        for(Question question: survey.getQuestions()){
            questions.add(question.getQuestion());
            types.add(question.getType().getResult());
            ans.add(question.getAnswers().getResult());
        }

        surveyDTO.setAnswers(ans);
        surveyDTO.setTypes(types);
        surveyDTO.setQuestions(questions);

        return new Response<>(surveyDTO, false, "OK");
    }

    /**
     * add a new rule to a survey
     * @param username the name of the user that desired to add a rule
     * @param id of the survey
     * @param rule the new rule to be added
     * @param goalID the goal the rule represents
     * @return success response if the arguments are legal. failure otherwise
     */
    public Response<Boolean> addRule(String username, int id, Rule rule, int goalID){
        Pair<Survey, FaultDetector> surveyPair;
        FaultDetector faultDetector;
        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, id);

        if(!legalAdd.getResult())
            return new Response<>(false, true, username + " does not created survey " + id);

        if(!surveys.containsKey(id))
            return new Response<>(false, true, "id is out of bound");

        surveyPair =surveys.get(id);
        faultDetector = surveyPair.getSecond();
        faultDetector.addRule(rule, goalID);

        surveys.put(id, new Pair<>(surveyPair.getFirst(), faultDetector));

        return new Response<>(true, false, "OK");
    }

    /**
     * remove a rule from a survey
     * @param username the name of the user desired to remove a rule
     * @param id of the survey
     * @param ruleID id of the rule to be removed
     * @return successful response if the {@username} created the survey in first place
     */
    public Response<Boolean> removeRule(String username, int id, int ruleID){
        Pair<Survey, FaultDetector> surveyPair;
        FaultDetector faultDetector;
        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, id);

        if(!legalAdd.getResult())
            return new Response<>(false, true, username + " does not created survey " + id);

        if(!surveys.containsKey(id))
            return new Response<>(false, true, "id is out of bound");

        surveyPair =surveys.get(id);
        faultDetector = surveyPair.getSecond();
        Response<Boolean> res = faultDetector.removeRule(ruleID);

        surveys.put(id, new Pair<>(surveyPair.getFirst(), faultDetector));

        return res;
    }

    /**
     * detects irregularities in survey answers
     * @param username the name of the user want to detect fault
     * @param id of the survey
     * @return response contains list of all goals that not consistent with the rules, for each answer
     */
    public Response<List<List<String>>> detectFault(String username, int id, String year){
        List<List<String>> faults = new LinkedList<>();
        FaultDetector faultDetector;
        List<GoalDTO> goals = UserController.getInstance().getGoals(username, year).getResult();
        List<String> currentFaults;
        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, id);

        if(!legalAdd.getResult())
            return new Response<>(null, true, username + " does not created survey " + id);

        if(!surveys.containsKey(id))
            return new Response<>(null, true, "The survey doesn't exists");

        faultDetector = surveys.get(id).getSecond();

        for(SurveyAnswers ans: answers.get(id)){
            currentFaults = new LinkedList<>();

            for(Integer fault: faultDetector.detectFault(ans).getResult())
                currentFaults.add(goals.get(fault).getTitle());

            faults.add(currentFaults);
        }

        return new Response<>(faults, false, "faults detected");
    }

    public Map<Integer, Pair<Survey, FaultDetector>> getSurveys() {
        return surveys;
    }

    public Map<Integer, List<SurveyAnswers>> getAnswers() {
        return answers;
    }

    public Response<List<SurveyAnswers>> getAnswersForSurvey(int surveyId) {
        if(answers.containsKey(surveyId)) {
            return new Response<>(answers.get(surveyId), false, "");
        }
        else{
            return new Response<>(null, true, "survey doesn't exist");
        }
    }

    public void setAnswers(Map<Integer, List<SurveyAnswers>> answers) {
        this.answers = answers;
    }

    /**
     * detect all irregularities in certain school
     * @param username the name of the user that want to detect the faults
     * @param id of the survey
     * @param symbol of the school
     * @return list of all goals that not consistent with the rules
     */
    public Response<List<Integer>> detectSchoolFault(String username, int id, String symbol, String year){
        FaultDetector faultDetector;
        List<GoalDTO> goals = UserController.getInstance().getGoals(username, year).getResult();
        List<Integer> currentFaults = new LinkedList<>();
        Response<Boolean> legalAdd = UserController.getInstance().hasCreatedSurvey(username, id);

        if(!legalAdd.getResult())
            return new Response<>(null, true, username + " does not created survey " + id);

        if(!surveys.containsKey(id))
            return new Response<>(null, true, "The survey doesn't exists");

        faultDetector = surveys.get(id).getSecond();

        for(SurveyAnswers ans: answers.get(id)){

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

    // for testing purpose only
    public void clearCache(){
        surveys.clear();
        answers.clear();
        indexer = 0;
    }
}
