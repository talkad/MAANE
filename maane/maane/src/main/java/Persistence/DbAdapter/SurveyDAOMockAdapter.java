package Persistence.DbAdapter;

import Communication.DTOs.QuestionDTO;
import Communication.DTOs.RuleDTO;
import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.Rules.Comparison;
import Domain.DataManagement.FaultDetector.Rules.RuleType;
import Persistence.SurveyPersistence;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static Domain.DataManagement.AnswerState.AnswerType.MULTIPLE_CHOICE;
import static Domain.DataManagement.AnswerState.AnswerType.NUMERIC_ANSWER;

/**
 * cache optimization for DataManagement Module
 * The main DTOs of this module, SurveyDTO, SurveyAnswersDTO, will  be cached
 *
 * Parallelism optimization:
 * for each function related to cache, the update of DB will occur concurrently.
 */
public class SurveyDAOMockAdapter {


    private static class CreateSafeThreadSingleton {
        private static final SurveyDAOMockAdapter INSTANCE = new SurveyDAOMockAdapter();
    }

    public static SurveyDAOMockAdapter getInstance() {
        return SurveyDAOMockAdapter.CreateSafeThreadSingleton.INSTANCE;
    }



    public List<Pair<RuleDTO, Integer>> getRules(String surveyID) {
        return  Arrays.asList(
                new Pair<>(new RuleDTO(null, RuleType.NUMERIC, Comparison.GREATER_THAN, 0, List.of(28)), 0),
                new Pair<>(new RuleDTO(null, RuleType.MULTIPLE_CHOICE, Comparison.GREATER_THAN, 1, List.of(1)), 1),
                new Pair<>(new RuleDTO(null, RuleType.MULTIPLE_CHOICE, Comparison.GREATER_THAN, 2, List.of(2)), 2)
        );
    }


    public List<SurveyAnswersDTO> getAnswers(String surveyId) {
        SurveyAnswersDTO answersDTO1 = new SurveyAnswersDTO();

        List<String> answers2 = Arrays.asList("30", "1", "2");
        List<AnswerType> types2 = Arrays.asList(NUMERIC_ANSWER, MULTIPLE_CHOICE, MULTIPLE_CHOICE);
        answersDTO1.setId("");
        answersDTO1.setAnswers(answers2);
        answersDTO1.setTypes(types2);
        answersDTO1.setId(surveyId);

        return Arrays.asList(answersDTO1, answersDTO1);
    }


}

