package Domain.DataManagement.FaultDetector.Rules;

import Domain.DataManagement.SurveyAnswers;

import java.util.List;

public class AndRule implements Rule{

    private List<Rule> rules;

    public AndRule(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public boolean apply(SurveyAnswers answers) {

        for(Rule rule: rules){
            if(!rule.apply(answers))
                return false;
        }

        return true;
    }
}
