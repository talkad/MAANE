package Communication.DTOs;

import Domain.CommonClasses.Pair;
import Domain.UsersManagment.Activity;
import Domain.WorkPlan.WorkPlan;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@NoArgsConstructor
@AllArgsConstructor

public class WorkPlanDTO {
    private List<Pair<LocalDateTime, List<ActivityDTO>>> calendar; //List of <Date String, List of activities for that day

    public WorkPlanDTO(WorkPlan workPlan){ //TreeMap <String, List<Activity>>
        List<Pair<LocalDateTime, List<ActivityDTO>>> calendar = new Vector<>();
        for (Map.Entry<LocalDateTime, List<Activity>> oldEntry : workPlan.getCalendar().entrySet()) {
            List<Activity> oldActivities = oldEntry.getValue();
            List<ActivityDTO> newActivities = new Vector<>();
            for (Activity oldActivity : oldActivities) {
                ActivityDTO newActivity = new ActivityDTO(oldActivity.getSchool(), oldActivity.getTitle());
                newActivities.add(newActivity);
            }
            calendar.add(new Pair<>(oldEntry.getKey(), newActivities));
        }
        this.calendar = calendar;
    }

    public List<Pair<LocalDateTime, List<ActivityDTO>>> getCalendar(){
        return calendar;
    }

    public void printMe(){
        for (Pair<LocalDateTime, List<ActivityDTO>> pair : calendar){
            System.out.println("Date: " + pair.getFirst());
            for (ActivityDTO activityDTO : pair.getSecond()){
                System.out.println(" Activity: " + activityDTO.toString());
            }
        }
    }
}
