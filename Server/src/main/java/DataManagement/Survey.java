package DataManagement;

import java.util.List;

public class Survey {

    private List<Question> questions;

    public Question getQuestion(int index){
        return this.questions.get(index);
    }
}
