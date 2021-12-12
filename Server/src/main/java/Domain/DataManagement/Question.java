package Domain.DataManagement;

import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.Answer;
import Domain.DataManagement.AnswerState.AnswerSingleChoice;
import java.util.List;

public class Question {

    private int id;
    private String question;
    private Answer answer;

    public Question(int id, String question){
        this.id = id;

        this.question = question;
    }

    //  implement all functions

    public String getQuestion() {
        return question;
    }



    public int getId () { return id; }

    public void setId (int id) { this.id = id; }

    public void setQuestion (String question) { this.question = question; }
}
