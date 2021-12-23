package Domain.DataManagement;

import Communication.DTOs.SurveyAnswersDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;

import java.util.HashMap;
import java.util.Map;

public class SurveyAnswers {

    private String symbol;
    private Map<Integer, Pair<AnswerType, String>> answers;
    private int indexer;

    public SurveyAnswers(){
        indexer = 0;
        answers = new HashMap<>();
    }

    public Response<String> getAnswer(int index){
        if(index >= answers.size())
            return new Response<>("", true, "index out of bound");

        return new Response<>(answers.get(index).getSecond(), false, "OK");
    }

    public Response<AnswerType> getAnswerType(int index){
        if(index >= answers.size())
            return new Response<>(AnswerType.NONE, true, "index out of bound");

        return new Response<>(answers.get(index).getFirst(), false, "OK");
    }

    public Response<Boolean> addAnswers(SurveyAnswersDTO answersDTO){
        for(int i = 0; i < answersDTO.getTypes().size(); i++){
            if(addAnswer(answersDTO.getTypes().get(i), answersDTO.getAnswers().get(i)).isFailure())
                return new Response<>(false, true, "answer " + i + " is not consistent with type");
        }

        return new Response<>(true, false, "OK");
    }

    private Response<Boolean> addAnswer(AnswerType type, String answer){
        switch (type){
            case MULTIPLE_CHOICE:
            case NUMERIC_ANSWER:
                if(!isNumeric(answer))
                    return new Response<>(false, true, "answer is not a number");

                answers.put(indexer++, new Pair<>(type, answer));
                return new Response<>(true, false, "OK");
            case VERBAL_ANSWER:
                answers.put(indexer++, new Pair<>(type, answer));
                return new Response<>(true, false, "OK");
        }

        return new Response<>(false, true, "answer is not consistent with type");
    }

    public boolean isNumeric(String num)
    {
        try
        {
            Integer.parseInt(num);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
