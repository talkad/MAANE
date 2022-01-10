package Communication.DTOs;

import Domain.CommonClasses.Pair;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor

public class WorkPlanDTO {
    private List<Pair<String, List<ActivityDTO>>> calendar; //List of <Date String, List of activities for that day

    public List<Pair<String, List<ActivityDTO>>> getCalendar(){
        return calendar;
    }
}
