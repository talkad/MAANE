package Communication.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActivityDTO {
    String schoolId;
    String title;

    public String toString (){
        return "School: "+schoolId+" Title: "+title;
    }
}
