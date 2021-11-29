package Domain.DataManagement.FaultDetector.Rules;

import Domain.DataManagement.Survey;

import java.util.List;

public class OrRule implements Rule{
    private List<Rule> rules;

    public OrRule(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public boolean apply(Survey survey) {

        for(Rule rule: rules){
            if(rule.apply(survey))
                return true;
        }

        return false;
    }
}
