package Communication.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActivityDTO {
    String schoolId;
    Integer goalId;
    String title;

    public String toString (){
        return "School: " + schoolId + "GoalId: " + goalId + " Title: " + title;
    }
}
