package DataManagement.FaultDetector.Rules;

import DataManagement.Survey;

public class BaseRule implements Rule{

    private int questionID;
    private int answerID;

    public BaseRule(int questionID, int answerID) {
        this.questionID = questionID;
        this.answerID = answerID;
    }

    @Override
    public boolean apply(Survey survey) {
        return survey.getQuestion(questionID).getAnswer(answerID).isSelected();
    }
}
