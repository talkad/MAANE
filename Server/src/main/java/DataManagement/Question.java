package DataManagement;

import java.util.LinkedList;
import java.util.List;

public class Question {

    private int id; //better not to have this...
    private int indexer;
    private String question;
    private List<Answer> answers;

    public Question(int id, String question, List<String> answers){
        this.id = id;
        this.indexer = 0;
        this.question = question;
        this.answers = new LinkedList<>();

        for(String answer: answers){
            addAnswer(answer);
        }
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void updateAnswer(int index, String answer){
        this.answers.get(index).setAns(answer);
    }

    public void addAnswer(String answer){
        this.answers.add(new Answer(indexer, answer));
        this.indexer++;
    }

    public void removeAnswer(int index){
        Answer currentAns;
        this.answers.remove(index);

        // update the index of the following answers
        for(int i = index; i < this.answers.size(); i++){
            currentAns = this.answers.get(i);
            currentAns.setId(currentAns.getId() - 1);
        }

        this.indexer --;
    }

    public void markAnswer(int index){
        answers.get(index).markAnswer();
    }

    public void cancelAnswer(int index){
        answers.get(index).cancelAnswer();
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
