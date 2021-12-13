package Domain.DataManagement.AnswerState;

import Domain.CommonClasses.Response;

import java.util.Collection;
import java.util.List;


public interface Answer {

    Response<Boolean> addAnswer(String answer);

    Response<Boolean> removeAnswer (int answerID);

    Response<Collection<String>> getAnswers();

    Response<AnswerType> getType();

    Response<Boolean> defineType(AnswerType type);
}
