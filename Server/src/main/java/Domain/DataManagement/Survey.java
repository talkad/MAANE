package Domain.DataManagement;

import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;

import java.util.List;
import java.time.Year;
import java.util.LinkedList;

import static Domain.DataManagement.AnswerState.AnswerType.NUMERIC_ANSWER;
import static Domain.DataManagement.AnswerState.AnswerType.VERBAL_ANSWER;


public class Survey {

    private String title;
    private String description;
    private List<Question> questions;
    private final int index;
    private int version; // usually the version will be the current year
    private int indexer;

    public Question getQuestion(int index){
        return this.questions.get(index);
    }


    private Survey (int index, String title, String description){
        this.title = title;
        this.description = description;
        this.index = index;
        this.version = Year.now().getValue();
        this.questions = new LinkedList<>();
        this.indexer = 0;
    }

    public static Response<Survey> createSurvey(int index, String title, String description){
        if(title.length() == 0)
            return new Response<>(null, true, "title cannot be empty");

        if(description.length() == 0)
            return new Response<>(null, true, "description cannot be empty");

        return new Response<>(new Survey(index, title, description), false, "OK");
    }

    public static Response<Survey> createSurvey(int index, SurveyDTO surveyDTO){
        Response<Integer> questionRes;
        Response<Boolean> answerRes;
        Response<Survey> surveyRes = createSurvey(index, surveyDTO.getTitle(), surveyDTO.getDescription());
        Survey survey = surveyRes.getResult();

        if(surveyRes.isFailure())
            return surveyRes;

        for(int i = 0; i < surveyDTO.getQuestions().size(); i++){
            questionRes = survey.addQuestion(surveyDTO.getQuestions().get(i));

            if(questionRes.isFailure())
                return new Response<>(null, true, questionRes.getErrMsg());

            switch(surveyDTO.getTypes().get(i)){
                case MULTIPLE_CHOICE:
                    for(String ans: surveyDTO.getAnswers().get(i)){
                        answerRes = survey.addAnswer(questionRes.getResult(), ans);

                        if(answerRes.isFailure())
                            return new Response<>(null, true, answerRes.getErrMsg());
                    }
                    break;

                case VERBAL_ANSWER:
                    answerRes = survey.addOpenAnswer(questionRes.getResult(), VERBAL_ANSWER);

                    if(answerRes.isFailure())
                        return new Response<>(null, true, answerRes.getErrMsg());
                    break;

                case NUMERIC_ANSWER:
                    answerRes = survey.addOpenAnswer(questionRes.getResult(), NUMERIC_ANSWER);

                    if(answerRes.isFailure())
                        return new Response<>(null, true, answerRes.getErrMsg());
                    break;
            }
        }

        return new Response<>(survey, false, "OK");
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

    public Response<Boolean> addAnswer (int questionID, String answer){

        if(questions.size() <= questionID)
            return new Response<>(false, true, "question doesn't exist");

        return questions.get(questionID).addAnswer(answer);
    }

    public Response<Boolean> addOpenAnswer (int questionID, AnswerType type){

        if(questions.size() <= questionID)
            return new Response<>(false, true, "question doesn't exist");

        return questions.get(questionID).defineType(type);
    }

    public Response<Boolean> removeAnswer (int questionID, int answerID){
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIndex() {
        return index;
    }
}
