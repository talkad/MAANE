package Domain.DataManagement.FaultDetector.Rules;

public enum Comparison {
    GREATER_THEN("GREATER_THEN"),
    LESS_THEN("LESS_THEN"),
    EQUAL("EQUAL");

    private final String compare;

    Comparison(String compare){
        this.compare = compare;
    }

    public String getComparison(){
        return this.compare;
    }
}
