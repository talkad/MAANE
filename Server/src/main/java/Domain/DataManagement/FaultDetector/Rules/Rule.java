package Domain.DataManagement.FaultDetector.Rules;

import Domain.DataManagement.Survey;

public interface Rule {

    public boolean apply(Survey survey);
}
