package Domain.DataManagement;

import Domain.CommonClasses.Response;

import java.util.LinkedList;
import java.util.List;

public class Question {

    private int id; //better not to have this...
    private int indexer;
    private String question;
    private List<Answer> answers;

    public Question(int id, String question){
        this.id = id;
        this.indexer = 0;
        this.question = question;
        this.answers = new LinkedList<>();
    }

    public Response<Integer> addAnswer(String answer){

        if(answer.length() == 0)
            return new Response<>(-1, true, "answer cannot be empty");

        this.answers.add(new Answer(indexer, answer));
        return new Response<>(indexer++, true, "answer cannot be empty");
    }

    public Response<Boolean> removeAnswer (int answerID){
        Answer answer;

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

    public Response<Boolean> markAnswer(int answerID){
        if(answers.size() <= answerID)
            return new Response<>(false, true, "answer doesn't exist");

        answers.get(answerID).markAnswer();
        return new Response<>(true, false, "answer marked successfully");
    }

    public Response<Boolean> cancelAnswer(int answerID){
        if(answers.size() <= answerID)
            return new Response<>(false, true, "answer doesn't exist");

        answers.get(answerID).cancelAnswer();
        return new Response<>(true, false, "answer canceled successfully");
    }

    public String getQuestion() {
        return question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public Answer getAnswer(int index) {
        return this.answers.get(index);
    }

    public int getId () { return id; }

    public void setId (int id) { this.id = id; }
}
