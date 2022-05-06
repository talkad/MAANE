package Communication.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnswersDTO {

    private List<String> answers;
    private List<Boolean> isLegal;
    private List<String> goals;

}
