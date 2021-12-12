package Domain.DataManagement.AnswerState;

import Domain.CommonClasses.Response;

import java.util.LinkedList;
import java.util.List;

public class AnswerMultipleChoice implements Answer{

    private int indexer;
    private List<AnswerSingleChoice> answers;

    public AnswerMultipleChoice() {
        this.indexer = 0;
        this.answers = new LinkedList<>();
    }

    @Override
    public Response<Integer> addAnswer(String answer){

        if(answer.length() == 0)
            return new Response<>(-1, true, "answer cannot be empty");

        this.answers.add(new AnswerSingleChoice(indexer, answer));
        return new Response<>(indexer++, true, "answer cannot be empty");
    }

    @Override
    public Response<Boolean> removeAnswer (int answerID){
        AnswerSingleChoice answer;

        if(answers.size() <= answerID)
            return new Response<>(false, true, "answer doesn't exist");

        this.answers.remove(answerID);

        // update the index of the following answers
        for(int i = answerID; i < this.answers.size(); i++){
            answer = this.answers.get(i);
            answer.setId(answer.getId() - 1);
        }
        indexer--;

        return new Response<>(true, false, "removed question successfully");
    }

    @Override
    public Response<Boolean> markAnswer(int answerID){
        if(answers.size() <= answerID)
            return new Response<>(false, true, "answer doesn't exist");

        answers.get(answerID).markAnswer();
        return new Response<>(true, false, "answer marked successfully");
    }

    @Override
    public Response<Boolean> cancelAnswer(int answerID){
        if(answers.size() <= answerID)
            return new Response<>(false, true, "answer doesn't exist");

        answers.get(answerID).cancelAnswer();
        return new Response<>(true, false, "answer canceled successfully");
    }

    @Override
    public Response<List<AnswerSingleChoice>> getAnswers() {
        return new Response<>(answers, false, "OK");
    }

    @Override
    public Response<AnswerSingleChoice> getAnswer(int index) {
        return new Response<>(this.answers.get(index), false, "OK");
    }

    @Override
    public Response<String> getAnswer() {
        return new Response<>("", true, "wrong answer type");
    }

    @Override
    public Response<Boolean> fillAnswer(String answer) {
        return new Response<>(false, true, "wrong answer type");
    }

}
