package DataManagement;

public class Answer {

    private int id;
    private String answer;
    private boolean isSelected;

    public Answer(int id, String answer) {
        this.id = id;
        this.answer = answer;
        this.isSelected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAns(String ans) {
        this.answer = ans;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void markAnswer(){
        this.isSelected = true;
    }

    public void cancelAnswer(){
        this.isSelected = false;
    }
}
