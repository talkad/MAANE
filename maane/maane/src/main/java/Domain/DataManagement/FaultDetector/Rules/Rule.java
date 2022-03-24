package Domain.DataManagement.FaultDetector.Rules;

import Domain.DataManagement.SurveyAnswers;

public interface Rule {

    boolean apply(SurveyAnswers answers);
}
