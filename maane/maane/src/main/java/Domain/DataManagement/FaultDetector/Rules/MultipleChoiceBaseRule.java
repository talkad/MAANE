package Domain.DataManagement.FaultDetector.Rules;

import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.SurveyAnswers;

import static Domain.DataManagement.AnswerState.AnswerType.MULTIPLE_CHOICE;

public class MultipleChoiceBaseRule implements Rule{

    private final int questionID;
    private final int answerID;

    public MultipleChoiceBaseRule(int questionID, int answerID) {
        this.questionID = questionID;
        this.answerID = answerID;
    }

    @Override
    public boolean apply(SurveyAnswers answers) {
        Response<AnswerType> type = answers.getAnswerType(questionID);

        if(type.isFailure() || type.getResult() != MULTIPLE_CHOICE)
            return false;

        return Integer.parseInt(answers.getAnswer(questionID).getResult()) == answerID;
    }
}