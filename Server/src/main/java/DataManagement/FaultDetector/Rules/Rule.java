package DataManagement.FaultDetector.Rules;

import DataManagement.Survey;

public interface Rule {

    public boolean apply(Survey survey);
}
