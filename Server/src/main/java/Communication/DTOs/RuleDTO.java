package Communication.DTOs;

import Domain.DataManagement.FaultDetector.Rules.Comparison;
import Domain.DataManagement.FaultDetector.Rules.RuleType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
public class RuleDTO {

//    private String title;
//    private int goalID;
    private List<RuleDTO> subRules;
    private RuleType type;
    private Comparison comparison;
    private int questionID;
    private int answer;

//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public int getGoalID() {
//        return goalID;
//    }
//
//    public void setGoalID(int goalID) {
//        this.goalID = goalID;
//    }

    public List<RuleDTO> getSubRules() {
        return subRules;
    }

    public void setSubRules(List<RuleDTO> subRules) {
        this.subRules = subRules;
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
