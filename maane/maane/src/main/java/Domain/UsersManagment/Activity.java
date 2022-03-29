package Domain.UsersManagment;

public class Activity {
    String schoolId;
    String title;

    public Activity (String school, String title){
        this.schoolId = school;
        this.title = title;
    }

    public String getSchool() {
        return schoolId;
    }

    public String getTitle() {
        return title;
    }

    public void setSchool(String school) {
        this.schoolId = school;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
