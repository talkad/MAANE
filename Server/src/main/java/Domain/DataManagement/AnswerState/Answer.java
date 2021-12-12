package Domain.DataManagement.AnswerState;

import Domain.CommonClasses.Response;

import java.util.List;


public interface Answer {

    Response<Integer> addAnswer(String answer);
    Response<Boolean> removeAnswer (int answerID);

    Response<Boolean> markAnswer(int answerID);

    Response<Boolean> cancelAnswer(int answerID);

    Response<List<AnswerSingleChoice>> getAnswers();

    Response<AnswerSingleChoice> getAnswer(int index);

    Response<String> getAnswer();

    Response<Boolean> fillAnswer(String answer);
}
