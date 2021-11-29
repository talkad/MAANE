package DataManagement.FaultDetector.Rules;

import DataManagement.Survey;

import java.util.List;

public class AndRule implements Rule{

    private List<Rule> rules;

    public AndRule(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public boolean apply(Survey survey) {

        for(Rule rule: rules){
            if(!rule.apply(survey))
                return false;
        }

        return true;
    }
}
