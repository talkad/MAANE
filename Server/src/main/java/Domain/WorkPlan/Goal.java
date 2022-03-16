package Domain.WorkPlan;

import Communication.DTOs.GoalDTO;

public class Goal {
    private int goalId;
    private String title;
    private String description;
    private int quarterly;
    private int weight;

    public Goal(int goalId, String title, String description, int quarterly, int weight) {
        this.goalId = goalId;
        this.title = title;
        this.description = description;
        this.quarterly = quarterly;
        this.weight = weight;
    }

    public Goal(int goalId, String title, String description, int quarterly) {
        this.goalId = goalId;
        this.title = title;
        this.description = description;
        this.quarterly = quarterly;
        this.weight = -1; //todo uninitialized
    }

    public Goal(String title, int weight){  //for test purposes only
        this.goalId = 1;
        this.title = title;
        this.description = "test";
        this.quarterly = 1;
        this.weight = weight;
    }

    public Goal(GoalDTO gDTO) {
        this.goalId = gDTO.getGoalId();
        this.title = gDTO.getTitle();
        this.description = gDTO.getDescription();
        this.quarterly = gDTO.getQuarterly();
        this.weight = gDTO.getWeight();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getQuarterly() {
        return quarterly;
    }

    public void setQuarterly(int quarterly) {
        this.quarterly = quarterly;
    }

    public String toString(){
        return /*"goal id: " + this.goalId + */" title: " + this.title + /*" description: " + this.description +*/ " weight:  " + this.weight;
    }
}
