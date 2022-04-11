package Domain.DataManagement.FaultDetector.Rules;

import Communication.DTOs.RuleDTO;
import Domain.DataManagement.SurveyAnswers;

public interface Rule {

    boolean apply(SurveyAnswers answers);

    RuleDTO getDTO();
}
