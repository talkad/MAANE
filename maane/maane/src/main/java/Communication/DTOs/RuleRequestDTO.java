package Communication.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class RuleRequestDTO {

    private int goalID;
    private RuleDTO ruleDTO;

}
