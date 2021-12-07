package Domain.DataManagement.FaultDetector;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Domain.DataManagement.Survey;
import java.util.LinkedList;
import java.util.List;

public class FaultDetector {

    private List<Pair<Rule, String>> rules;

    public FaultDetector() {
        this.rules = new LinkedList<>();
    }

    public Response<Boolean> addRule(Rule rule, String description){

        if(description.length() == 0)
            return new Response<>(false, true, "description cannot be empty");

        rules.add(new Pair<>(rule, description));
        return new Response<>(true, false, "rule added successfully");
    }

    public Response<Boolean> removeRule(int index){

        if(index >= rules.size())
            return new Response<>(false, true, "index out of bounds");

        rules.remove(index);
        return new Response<>(true, false, "rule removed successfully");
    }

    public Response<List<String>> detectFault(Survey survey){
        List<String> faults = new LinkedList<>();

        for(Pair<Rule, String> rule: rules){
            if(rule.getFirst().apply(survey))
                faults.add(rule.getSecond());
        }

        return new Response<>(faults, false, "details");
    }
}
