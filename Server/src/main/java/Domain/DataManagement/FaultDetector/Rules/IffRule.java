package Domain.DataManagement.FaultDetector.Rules;

import Domain.DataManagement.Survey;

public class IffRule implements Rule{
    private Rule firstSide, secondSide;

    public IffRule(Rule firstSide, Rule secondSide) {
        this.firstSide = firstSide;
        this.secondSide = secondSide;
    }

    @Override
    public boolean apply(Survey survey) {
        return firstSide.apply(survey) == secondSide.apply(survey);
    }
}
