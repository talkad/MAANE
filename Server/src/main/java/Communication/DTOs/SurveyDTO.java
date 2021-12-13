package Communication.DTOs;

import Domain.DataManagement.AnswerState.AnswerType;

import java.util.List;

//add annotations
public class SurveyDTO {

    private String title;
    private String description;
    private List<String> questions;
    private List<List<String>> answers;
    private List<AnswerType> types;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public List<List<String>> getAnswers() {
        return answers;
    }

    public List<AnswerType> getTypes() {
        return types;
    }
}
