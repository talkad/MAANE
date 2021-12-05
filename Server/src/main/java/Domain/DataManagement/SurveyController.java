package Domain.DataManagement;

import Domain.CommonClasses.Response;

import java.util.HashMap;
import java.util.Map;

public class SurveyController {
    private Map<Integer, Survey> surveys;
    private int indexer;

    public SurveyController(){
        surveys = new HashMap<>();
        indexer = 0;
    }

    public Response<Integer> createSurvey(String username, String title){
        // call userController and check if @username can create survey
        Response<Survey> surveyRes = Survey.createSurvey(indexer, title);

        if(surveyRes.isFailure())
            return new Response<>(-1, true, surveyRes.getErrMsg());

        surveys.put(indexer, surveyRes.getResult());

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

        return new Response<>(surveys.get(id), false, "The survey removed successfully ");
    }

    public Response<Survey> publishSurvey(String username){
        // return call to userController that notify a new survey
        return null;
    }

}
