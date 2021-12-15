package Domain.DataManagement;

import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.Answer;
import Domain.DataManagement.AnswerState.AnswerType;
import java.util.LinkedList;
import java.util.List;

public class Question {

    private int id;
    private String question;
    private Answer answer;

    public Question(int id, String question){
        this.id = id;
        this.question = question;
    }

    public Response<Boolean> addAnswer(String ans) {
        if(answer == null)
            return new Response<>(false, true, "answer wasn't initiated");

        return answer.addAnswer(ans);
    }

    public Response<Boolean> removeAnswer(int answerID) {
        if(answer == null)
            return new Response<>(false, true, "answer wasn't initiated");

        return answer.removeAnswer(answerID);
    }

    public Response<Boolean> defineType(AnswerType type) {
        if(answer == null)
            return new Response<>(false, true, "answer wasn't initiated");

        return answer.defineType(type);
    }

    public Response<List<String>> getAnswers() {
        if(answer == null)
            return new Response<>(new LinkedList<>(), true, "answer wasn't initiated");

        return answer.getAnswers();
    }

    public Response<AnswerType> getType() {
        if(answer == null)
            return new Response<>(AnswerType.NONE, true, "answer wasn't initiated");

        return answer.getType();
    }

    public String getQuestion() {
        return question;
    }

    public int getId () { return id; }

    public void setId (int id) { this.id = id; }

    public void setQuestion (String question) { this.question = question; }
}
