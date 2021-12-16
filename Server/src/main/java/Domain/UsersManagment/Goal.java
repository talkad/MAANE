package Domain.UsersManagment;

public class Goal {
    private int goalId;
    private String title;
    private String description;
    private int weight;

    public Goal(int goalId, String title, String description, int weight) {
        this.goalId = goalId;
        this.title = title;
        this.description = description;
        this.weight = weight;
    }

    public Goal(int goalId, String title, String description) {
        this.goalId = goalId;
        this.title = title;
        this.description = description;
        this.weight = -1; //todo uninitialized
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

    public String toString(){
        return "goal id: " + this.goalId + " title: " + this.title + " description: " + this.description;
    }
}
