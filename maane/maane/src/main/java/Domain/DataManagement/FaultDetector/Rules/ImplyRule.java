package Domain.DataManagement.FaultDetector.Rules;

import Domain.DataManagement.SurveyAnswers;

public class ImplyRule implements Rule{
    private Rule firstSide, secondSide;

    public ImplyRule(Rule firstSide, Rule secondSide) {
        this.firstSide = firstSide;
        this.secondSide = secondSide;
    }

    @Override
    public boolean apply(SurveyAnswers answers) {
        return !firstSide.apply(answers) || secondSide.apply(answers);
    }
}
