package Domain.DataManagement;

import Domain.CommonClasses.Response;

import java.util.List;
import java.time.Year;
import java.util.LinkedList;


public class Survey {

    private String title;
    private List<Question> questions;
    private final int index;
    private int version; // usually the version will be the current year
    private int indexer;

    public Question getQuestion(int index){
        return this.questions.get(index);
    }


    private Survey (int index, String title){
        this.title = title;
        this.index = index;
        this.version = Year.now().getValue();
        this.questions = new LinkedList<>();
        this.indexer = 0;
    }

    public static Response<Survey> createSurvey(int index, String title){
        if(title.length() == 0)
            return new Response<>(null, true, "title cannot be empty");

        return new Response<>(new Survey(index, title), false, "");
    }


    public Response<Integer> addQuestion (String questionText){

        if(questionText.length() == 0)
            return new Response<>(-1, true, "question cannot be empty");

        Question question = new Question(indexer, questionText);
        questions.add(question);

        return new Response<>(indexer++, false, "added question successfully");
    }

    public Response<Boolean> removeQuestion (int questionID){
        Question question;

        if(questions.size() <= questionID)
            return new Response<>(false, true, "question doesn't exist");

        this.questions.remove(questionID);

        // update the index of the following answers
        for(int i = questionID; i < this.questions.size(); i++){
            question = this.questions.get(i);
            question.setId(question.getId() - 1);
        }
        indexer--;

        return new Response<>(true, false, "removed question successfully");
    }

    public Response<Integer> addAnswer (int questionID, String answer){

        if(questions.size() <= questionID)
            return new Response<>(-1, true, "question doesn't exist");

        return questions.get(questionID).addAnswer(answer);
    }

    public Response<Boolean> removeQuestion (int questionID, int answerID){
        if(questions.size() <= questionID)
            return new Response<>(false, true, "question doesn't exist");

        return questions.get(questionID).removeAnswer(answerID);
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
