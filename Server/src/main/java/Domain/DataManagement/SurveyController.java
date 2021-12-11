package Domain.DataManagement;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.FaultDetector;
import Domain.DataManagement.FaultDetector.Rules.Rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyController {
    private Map<Integer, Pair<Survey, FaultDetector>> surveys;
    private int indexer;

    private static class CreateSafeThreadSingleton {
        private static final SurveyController INSTANCE = new SurveyController();
    }

    public static SurveyController getInstance() {
        return SurveyController.CreateSafeThreadSingleton.INSTANCE;
    }

    public SurveyController(){
        surveys = new HashMap<>();
        indexer = 0;
    }

    public Response<Integer> createSurvey(String username, String title){
        // call userController and check if @username can create survey
        Response<Survey> surveyRes = Survey.createSurvey(indexer, title);

        if(surveyRes.isFailure())
            return new Response<>(-1, true, surveyRes.getErrMsg());

        surveys.put(indexer, new Pair<>(surveyRes.getResult(), new FaultDetector()));

        return new Response<>(indexer++, false, "new survey created successfully");
    }

    public Response<Boolean> removeSurvey(String username, int id){
        // call userController and check if @username can be removed

        if(!surveys.containsKey(id))
            return new Response<>(false, true, "The survey doesn't exists");

        surveys.remove(id);
        //update the controller

        return new Response<>(true, false, "The survey removed successfully ");
    }

    public Response<Survey> getSurvey(int id){
        if(!surveys.containsKey(id))
            return new Response<>(null, true, "The survey doesn't exists");

        return new Response<>(surveys.get(id).getFirst(), false, "received survey successfully ");
    }

    public Response<Survey> publishSurvey(String username){
        // return call to userController that notify a new survey
        return null;
    }

    public Response<Integer> addQuestion (int id, String questionText){

        if(!surveys.containsKey(id))
            return new Response<>(-1, true, "the survey doesn't exists");

        return surveys.get(id).getFirst().addQuestion(questionText);
    }

    public Response<Boolean> removeQuestion(int id, int questionID){

        if(!surveys.containsKey(id))
            return new Response<>(false, true, "the survey doesn't exists");

        return surveys.get(id).getFirst().removeQuestion(questionID);
    }

    public Response<Integer> addAnswer (int id, int questionID, String answer){

        if(!surveys.containsKey(id))
            return new Response<>(-1, true, "the survey doesn't exists");

        return surveys.get(id).getFirst().addAnswer(questionID, answer);
    }

    public Response<Boolean> removeAnswer (int id, int questionID, int answerID){

        if(!surveys.containsKey(id))
            return new Response<>(false, true, "the survey doesn't exists");

        return surveys.get(id).getFirst().removeAnswer(questionID, answerID);
    }

    public Response<Boolean> addRule(String username, int id, Rule rule, String description){
        // call userController and check if @username can

        if(!surveys.containsKey(id))
            return new Response<>(false, true, "The survey doesn't exists");

        return surveys.get(id).getSecond().addRule(rule, description);
    }

    public Response<Boolean> removeRule(String username, int id, int index){
        // call userController and check if @username can

        if(!surveys.containsKey(id))
            return new Response<>(false, true, "The survey doesn't exists");

        return surveys.get(id).getSecond().removeRule(index);
    }

    public Response<List<String>> detectFault(String username, int id){
        Pair<Survey, FaultDetector> pairSurvey;

        // call userController and check if @username can

        if(!surveys.containsKey(id))
            return new Response<>(null, true, "The survey doesn't exists");

        pairSurvey = surveys.get(id);
        return pairSurvey.getSecond().detectFault(pairSurvey.getFirst());
    }

}
