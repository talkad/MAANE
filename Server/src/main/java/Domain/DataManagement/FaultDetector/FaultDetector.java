package Domain.DataManagement.FaultDetector;

import Domain.CommonClasses.Pair;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Domain.DataManagement.Survey;
import java.util.LinkedList;
import java.util.List;

public class FaultDetector {

    private List<Pair<Rule, String>> rules;

    public FaultDetector() {
        this.rules = new LinkedList<>();
    }

    public FaultDetector(List<Pair<Rule, String>> rules) {
        this.rules = rules;
    }

    public void addRule(Rule rule, String description){
        rules.add(new Pair<>(rule, description));
    }

    public void removeRule(int index){
        rules.remove(index);
    }

    public List<String> detectFault(Survey survey){
        List<String> faults = new LinkedList<>();

        for(Pair<Rule, String> rule: rules){
            if(rule.getFirst().apply(survey))
                faults.add(rule.getSecond());
        }

        return faults;
    }
}
