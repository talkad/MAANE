package Domain.DataManagement.AnswerState;

import Domain.CommonClasses.Response;

import java.util.LinkedList;
import java.util.List;

import static Domain.DataManagement.AnswerState.AnswerType.VERBAL_ANSWER;

public class AnswerOpen implements Answer{

    private String answer;
    private final AnswerType type;

    public AnswerOpen(String answer, AnswerType type) {
        this.answer = answer;
        this.type = type;
    }

    @Override
    public Response<Integer> addAnswer(String answer) {
        return new Response<>(-1, true, "wrong answer type");
    }

    @Override
    public Response<Boolean> removeAnswer(int answerID) {
        return new Response<>(false, true, "wrong answer type");
    }

    @Override
    public Response<Boolean> markAnswer(int answerID) {
        return new Response<>(false, true, "wrong answer type");
    }

    @Override
    public Response<Boolean> cancelAnswer(int answerID) {
        return new Response<>(false, true, "wrong answer type");
    }

    @Override
    public Response<List<AnswerSingleChoice>> getAnswers() {
        return new Response<>(new LinkedList<>(), true, "wrong answer type");
    }

    @Override
    public Response<AnswerSingleChoice> getAnswer(int index) {
        return new Response<>(null, true, "wrong answer type");
    }

    @Override
    public Response<String> getAnswer() {
        return new Response<>(answer, false, "OK");
    }

    @Override
    public Response<Boolean> fillAnswer(String answer) {
        if(type == VERBAL_ANSWER)
        {
            this.answer = answer;
            return new Response<>(true, false, "legal answer");
        }
        else{

            try{ // check if integer
                int num = Integer.parseInt(answer);
                this.answer = answer;

                return new Response<>(true, false, "legal answer");
            }catch (NumberFormatException e){
                return new Response<>(false, true, "illegal number format");
            }
        }

    }


}
