package Domain.DataManagement;

import java.util.List;
import java.time.Year;
import java.util.LinkedList;


public class Survey {

    private List<Question> questions;

    public Question getQuestion(int index){
        return this.questions.get(index);
    }

    int version; // usually the version will be the current year

    public Survey (){
        version = Year.now().getValue();
        questions = new LinkedList<>();
    }

    public void addQuestion (String questionText, List <String> answers){
        Question question = new Question(questions.size(), questionText, answers);
        questions.add(question);
    }

    public void removeQuestion (Question question){
        int decrementIndex = question.getId();
        questions.remove(question);
        for (Question q : questions){ // decrement all indexes above the removed question
            if (q.getId() > decrementIndex){ q.setId(q.getId() - 1); }
        }
    }

    public Question getQuestionByIndex (int index){
        if (index > questions.size())
            return null;
        else return questions.get(index);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}
