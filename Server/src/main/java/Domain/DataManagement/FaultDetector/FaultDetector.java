package Domain.DataManagement.FaultDetector;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.FaultDetector.Rules.Rule;
import Domain.DataManagement.SurveyAnswers;

import java.util.LinkedList;
import java.util.List;

public class FaultDetector {

    // the index of the rule is the index in list
    private List<Pair<Rule, Integer>> rules;

    public FaultDetector() {
        this.rules = new LinkedList<>();
    }

    /**
     * add rule to current detector
     * @param rule to be added
     * @param goalID the goal this rule enforcing
     * @return successful response
     */
    public Response<Boolean> addRule(Rule rule, int goalID){
        rules.add(new Pair<>(rule, goalID));
        
        return new Response<>(true, false, "rule added successfully");
    }

    /**
     * remove rule from current detector
     * @param index of the desired rule to be removed
     * @return successful response if the ruleID exists. failure otherwise
     */
    public Response<Boolean> removeRule(int index){

        if(index >= rules.size())
            return new Response<>(false, true, "index out of bounds");

        rules.remove(index);
        return new Response<>(true, false, "rule removed successfully");
    }

    /**
     * detect all faults in a given answers
     * @param answers on certain survey
     * @return list of all goals that were not consistent with the given rules
     */
    public Response<List<Integer>> detectFault(SurveyAnswers answers){
        List<Integer> faults = new LinkedList<>();

        for(Pair<Rule, Integer> rule: rules){
            if(rule.getFirst().apply(answers))
                faults.add(rule.getSecond());
        }

        return new Response<>(faults, false, "details");
    }
}
