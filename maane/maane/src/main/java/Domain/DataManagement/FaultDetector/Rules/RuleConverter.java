package Domain.DataManagement.FaultDetector.Rules;

import Communication.DTOs.RuleDTO;

import java.util.LinkedList;
import java.util.List;

public class RuleConverter {

    private static class CreateSafeThreadSingleton {
        private static final RuleConverter INSTANCE = new RuleConverter();
    }

    public static RuleConverter getInstance() {
        return RuleConverter.CreateSafeThreadSingleton.INSTANCE;
    }

    public Rule convertRule(RuleDTO ruleDTO) {
        Rule rule;

        switch (ruleDTO.getType()){
            case AND:
                rule = ANDRuleConverter(ruleDTO);
                break;
            case OR:
                rule = OrRuleConverter(ruleDTO);
                break;
            case IFF:
                rule = IffRuleConverter(ruleDTO);
                break;
            case IMPLY:
                rule = ImplyRuleConverter(ruleDTO);
                break;
            case NUMERIC:
                rule = NumericRuleConverter(ruleDTO);
                break;
            case MULTIPLE_CHOICE:
                rule = MultipleChoiceRuleConverter(ruleDTO);
                break;
            default:
                rule = null;
        }

        return rule;
    }

    private Rule ANDRuleConverter(RuleDTO rule) {
        List<Rule> rules = new LinkedList<>();
        Rule currentRule;

        for(RuleDTO dto: rule.getSubRules())
        {
            currentRule = convertRule(dto);

            if(currentRule == null)
                return null;
            rules.add(currentRule);
        }

        return new AndRule(rules);
    }

    private OrRule OrRuleConverter(RuleDTO rule) {
        List<Rule> rules = new LinkedList<>();
        Rule currentRule;

        for(RuleDTO dto: rule.getSubRules())
        {
            currentRule = convertRule(dto);

            if(currentRule == null)
                return null;
            rules.add(currentRule);
        }


        return new OrRule(rules);
    }

    private IffRule IffRuleConverter(RuleDTO rule) {
        if(rule.getSubRules().size() != 2)
            return null;

        return new IffRule(convertRule(rule.getSubRules().get(0)), convertRule(rule.getSubRules().get(1)));
    }

    private IffRule ImplyRuleConverter(RuleDTO rule) {
        if(rule.getSubRules().size() != 2)
            return null;

        return new IffRule(convertRule(rule.getSubRules().get(0)), convertRule(rule.getSubRules().get(1)));
    }

    private MultipleChoiceBaseRule MultipleChoiceRuleConverter(RuleDTO rule) {
        return new MultipleChoiceBaseRule(rule.getQuestionID(), rule.getAnswer());
    }

    private NumericBaseRule NumericRuleConverter(RuleDTO rule) {
        return new NumericBaseRule(rule.getQuestionID(), rule.getComparison(), rule.getAnswer());
    }


}