package Domain.WorkPlan;

public class Activity {
    String schoolId;
    Integer goalId;
    String title;

    public Activity (String school, Integer goalId, String title){
        this.schoolId = school;
        this.goalId = goalId;
        this.title = title;
    }

    public String getSchool() {
        return schoolId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getGoalId() {
        return goalId;
    }

    public void setGoalId(Integer goalId) {
        this.goalId = goalId;
    }

    public void setSchool(String school) {
        this.schoolId = school;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
