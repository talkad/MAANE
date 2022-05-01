package Communication.DTOs;

import Domain.CommonClasses.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnswersDTO {

    /**
     * the inner list is for single answer
     * the second inner list contain the illegal rules this answer doesn't obey
     */
    private List<Pair<List<String>, List<Integer>>> answers;


}
