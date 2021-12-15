package Domain.DataManagement;

import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.FaultDetector;
import Domain.DataManagement.FaultDetector.Rules.Rule;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        answers = new HashMap<>();
        surveys = new HashMap<>();
        indexer = 0;
    }

    public Response<Integer> createSurvey(String username, SurveyDTO surveyDTO){

        // call userController and check if @username can create survey
        Response<Survey> surveyRes = Survey.createSurvey(indexer, surveyDTO);

        if(surveyRes.isFailure())
            return new Response<>(-1, true, surveyRes.getErrMsg());

        surveys.put(indexer, new Pair<>(surveyRes.getResult(), new FaultDetector()));

        // call to publish

        return new Response<>(indexer++, false, "new survey created successfully");
    }

    public Response<Boolean> addAnswers(SurveyAnswersDTO answersDTO){

        SurveyAnswers answer = new SurveyAnswers();
        Response<Boolean> answerRes = answer.addAnswers(answersDTO);

        if(answerRes.isFailure())
            return new Response<>(false, true, answerRes.getErrMsg());

        if(answers.containsKey(answersDTO.getId()))
            answers.put(answersDTO.getId(), new LinkedList<>());

        List<SurveyAnswers> ans = answers.get(answersDTO.getId());
        ans.add(answer);
        answers.put(answersDTO.getId(), ans);

        return new Response<>(true, false, "the answer added successfully");
    }

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

    public Response<Boolean> addRule(String username, int id, Rule rule, int goalID){
        Pair<Survey, FaultDetector> surveyPair;
        FaultDetector faultDetector;

        // check if username is supervisor

        if(!surveys.containsKey(id))
            return new Response<>(false, true, "id is out of bound");

        surveyPair =surveys.get(id);
        faultDetector = surveyPair.getSecond();
        faultDetector.addRule(rule, goalID);

        surveys.put(id, new Pair<>(surveyPair.getFirst(), faultDetector));

        return new Response<>(true, false, "OK");
    }

    public Response<List<List<String>>> detectFault(String username, int id){
        List<List<String>> faults = new LinkedList<>();
        FaultDetector faultDetector;
        // TODO get from userController all the goals of a certain username
        Map<Integer, String> goals;
        List<String> currentFaults;

        // TODO
        // call userController and check if @username can

        if(!surveys.containsKey(id))
            return new Response<>(null, true, "The survey doesn't exists");

        faultDetector = surveys.get(id).getSecond();

        for(SurveyAnswers ans: answers.get(id)){
            currentFaults = new LinkedList<>();

            for(Integer fault: faultDetector.detectFault(ans).getResult())
                currentFaults.add(currentFaults.get(fault));

            faults.add(currentFaults);
        }

        return new Response<>(faults, false, "faults detected");
    }


}
