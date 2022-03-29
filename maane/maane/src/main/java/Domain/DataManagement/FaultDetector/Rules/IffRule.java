package Domain.DataManagement.FaultDetector.Rules;


import Domain.DataManagement.SurveyAnswers;

public class IffRule implements Rule{
    private Rule firstSide, secondSide;

    public IffRule(Rule firstSide, Rule secondSide) {
        this.firstSide = firstSide;
        this.secondSide = secondSide;
    }

    @Override
    public boolean apply(SurveyAnswers answers) {
        return firstSide.apply(answers) == secondSide.apply(answers);
    }
}
