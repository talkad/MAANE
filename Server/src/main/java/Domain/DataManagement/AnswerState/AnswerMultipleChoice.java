package Domain.DataManagement.AnswerState;

import Domain.CommonClasses.Response;

import java.util.LinkedList;
import java.util.List;

public class AnswerMultipleChoice implements Answer{

    private List<String> answers;

    public AnswerMultipleChoice() {
        this.answers = new LinkedList<>();
    }

    @Override
    public Response<Boolean> addAnswer(String answer){

        if(answer.length() == 0)
            return new Response<>(false, true, "answer cannot be empty");

        this.answers.add(answer);
        return new Response<>(false, true, "answer cannot be empty");
    }

    @Override
    public Response<Boolean> removeAnswer (int answerID){

        if(answers.size() <= answerID)
            return new Response<>(false, true, "answer doesn't exist");

        this.answers.remove(answerID);

        return new Response<>(true, false, "removed question successfully");
    }

    @Override
    public Response<List<String>> getAnswers() {
        return new Response<>(answers, false, "OK");
    }


}
