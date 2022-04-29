package Persistence;

import Communication.DTOs.QuestionDTO;
import Communication.DTOs.RuleDTO;
import Communication.DTOs.SurveyAnswersDTO;
import Communication.DTOs.SurveyDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.DataManagement.AnswerState.AnswerType;
import Domain.DataManagement.FaultDetector.Rules.Comparison;
import Domain.DataManagement.FaultDetector.Rules.RuleType;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class SurveyQueries {

    /**
     * The cache will be implemented as LRU
     */

    private final Map<String, Pair<LocalDateTime, SurveyDTO>> surveys;

    /**
     * maximal size of cache
     */
    private final int cacheSize = 50;


    private static class CreateSafeThreadSingleton {
        private static final SurveyQueries INSTANCE = new SurveyQueries();
    }

    public static SurveyQueries getInstance() {
        return SurveyQueries.CreateSafeThreadSingleton.INSTANCE;
    }

    public SurveyQueries() {
        this.surveys = new ConcurrentHashMap<>();
    }

    //========================== Survey ==========================

    public Response<Boolean> insertSurvey(SurveyDTO surveyDTO) {
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"Surveys\" (id, title, description) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, surveyDTO.getId());
            preparedStatement.setString(2, surveyDTO.getTitle());
            preparedStatement.setString(3, surveyDTO.getDescription());
            rows = preparedStatement.executeUpdate();

            insertQuestions(surveyDTO.getId(), surveyDTO.getQuestions());

            insertAnswers(surveyDTO.getId(), ListListToStringList(surveyDTO.getAnswers()), surveyDTO.getTypes());
            Connect.closeConnection();
        } catch (SQLException e) {e.printStackTrace();}

        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public void insertQuestions (String surveyId, List<String> questions) {
        String sql = "INSERT INTO \"Questions\" (survey_id, index, question) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            for (String question : questions){
                preparedStatement.setString(1, surveyId);
                preparedStatement.setInt(2, questions.indexOf(question));
                preparedStatement.setString(3, question);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {e.printStackTrace();}
    }

    public Response<Boolean> addQuestion(QuestionDTO questionDTO, int question_index) {
        String sql = "INSERT INTO \"Questions\" (survey_id, index, question) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, questionDTO.getSurveyID());
            preparedStatement.setInt(2, question_index);
            preparedStatement.setString(3, questionDTO.getQuestion());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return new Response<>(false, true, "failed to add question");
        }

        String sql2 = "INSERT INTO \"MultiChoices\" (survey_id, question_index, answer_type, choices) VALUES (?, ?, ?, ?)";
        preparedStatement = null;
        try{
            preparedStatement = Connect.conn.prepareStatement(sql2);
            preparedStatement.setString(1, questionDTO.getSurveyID());
            preparedStatement.setInt(2, question_index);
            preparedStatement.setString(3, questionDTO.getType().getType());
            preparedStatement.setString(4, questionDTO.getQuestion());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return new Response<>(false, true, "failed to add question");
        }

        return new Response<>(false, true, "inserted question successfully");
    }

    public Response<Boolean> removeQuestions(String surveyID, int questionID) {
        String sql = "DELETE FROM  \"Questions\" WHERE survey_id=? and index=?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, surveyID);
            preparedStatement.setInt(2, questionID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            return new Response<>(false, true, "question deletion failed");
        }

        return new Response<>(true, false, "question removed successfully");
    }

    public void insertAnswers (String surveyId, List<String> answers, List<AnswerType> answerTypes) {

        String sql = "INSERT INTO \"MultiChoices\" (survey_id, question_index, answer_type, choices) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            for (String answer: answers) {
                preparedStatement.setString(1, surveyId);
                preparedStatement.setInt(2, answers.indexOf(answer));
                preparedStatement.setString(3, answerTypes.get(answers.indexOf(answer)).toString());
                preparedStatement.setString(4, answer);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {e.printStackTrace();}
    }

    private List<String> ListListToStringList(List<List<String>> l){
        List<String> result = new LinkedList<>();

        for(List<String> list: l){
            result.add(list.toString());
        }
        return result;
    }

    public Response<SurveyDTO> getSurvey(String id)  {
        Connect.createConnection();
        String sqlSurvey = "SELECT * FROM \"Surveys\" WHERE id = ?";
        try {
            PreparedStatement statement = Connect.conn.prepareStatement(sqlSurvey);
            statement.setString(1, id);
            ResultSet resultSurvey = statement.executeQuery();
            SurveyDTO surveyDTO = new SurveyDTO();

            if (resultSurvey.next()) {
                surveyDTO.setId(resultSurvey.getString("id"));
                surveyDTO.setTitle(resultSurvey.getString("title"));
                surveyDTO.setDescription(resultSurvey.getString("description"));
                List<String> questions = getSurveyQuestions(id);
                List<List<String>> answers = getSurveyAnswers(id, questions);
                surveyDTO.setQuestions(questions);
                surveyDTO.setAnswers(answers);

                Connect.closeConnection();
            } else {
                Connect.closeConnection();
                return new Response<>(null, true, "failed to get survey");
            }

            return new Response<>(surveyDTO, false, "survey loaded successfully");
        } catch(SQLException e) {
            return new Response<>(null, true, "Failed to get survey");
        }
    }

    private List<String> getSurveyQuestions (String survey_id) throws SQLException {
        String query = "SELECT * FROM \"Questions\" WHERE survey_id = ?";
        PreparedStatement statement = Connect.conn.prepareStatement(query);
        statement.setString(1, survey_id);
        ResultSet result = statement.executeQuery();
        List<String> questions = new LinkedList<>();

        while (result.next()) {
            // int index = result.getInt("index");
            String question = result.getString("question");
            questions.add(question);
        }
        return questions;
    }

    private List<List<String>> getSurveyAnswers (String survey_id, List<String> questions) throws SQLException {
        String query = "SELECT * FROM \"MultiChoices\" WHERE survey_id = ?";
        PreparedStatement statement = Connect.conn.prepareStatement(query);
        statement.setString(1, survey_id);
        ResultSet result = statement.executeQuery();
        List<List<String>> answers = new LinkedList<>();

        while (result.next()) {
            String answer = result.getString("choices");
            answer = answer.substring(1,answer.length()-1);
            String [] optionsArr = answer.split(",");
            List<String> optionsList = new LinkedList<>(Arrays.asList(optionsArr));
            answers.add(optionsList);
        }
        return answers;
    }

    //===========================================================

    //========================== Rules ==========================

    public void insertRule(String survey_id, int goalID, RuleDTO dto) {
        Connect.createConnection();
        String sql = "INSERT INTO \"Rules\" (survey_id, goal_id, type, comparison, question_id, answer) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, survey_id);
            preparedStatement.setInt(2, goalID);
            preparedStatement.setString(3, dto.getType().getType());
            preparedStatement.setString(4, dto.getComparison().getComparison());
            preparedStatement.setInt(5, dto.getQuestionID());
            preparedStatement.setString(6, dto.getAnswers().toString());
            preparedStatement.execute();

            ResultSet last_updated_person = preparedStatement.getResultSet();
            last_updated_person.next();
            int last_updated_person_id = last_updated_person.getInt(1);
            for (RuleDTO rule : dto.getSubRules()) {
                insertSubRule(survey_id, goalID, rule, last_updated_person_id);
            }

            Connect.closeConnection();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void insertSubRule(String survey_id, int goalID, RuleDTO dto, int parent_id) {
        String sql = "INSERT INTO \"Rules\" (survey_id, goal_id, type, comparison, question_id, answer, parent_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, survey_id);
            preparedStatement.setInt(2, goalID);
            preparedStatement.setString(3, dto.getType().getType());
            preparedStatement.setString(4, dto.getComparison().getComparison());
            preparedStatement.setInt(5, dto.getQuestionID());
            preparedStatement.setString(6, dto.getAnswers().toString());
            preparedStatement.setInt(7, parent_id);
            preparedStatement.execute();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Response<Boolean> removeRule (int ruleID) {
        Connect.createConnection();
        String sql = "DELETE FROM \"Rules\" WHERE id = ?";
        int rows = 0;
        try (PreparedStatement pstmt = Connect.conn.prepareStatement(sql)) {
            pstmt.setInt(1, ruleID);
            rows = pstmt.executeUpdate();
            removeSubRules(ruleID);

            Connect.closeConnection();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    public Response<Boolean> removeRules(String surveyID) {
        Connect.createConnection();
        String sql = "DELETE FROM \"Rules\" WHERE survey_id = ?";
        int rows = 0;
        try (PreparedStatement pstmt = Connect.conn.prepareStatement(sql)) {
            pstmt.setString(1, surveyID);
            rows = pstmt.executeUpdate();

            Connect.closeConnection();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    private void removeSubRules (int parentId){
        String sql = "DELETE FROM \"Rules\" WHERE parent_id = ?";
        try (PreparedStatement pstmt = Connect.conn.prepareStatement(sql)) {
            pstmt.setInt(1, parentId);
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<Pair<RuleDTO, Integer>> getRules(String surveyID) {
        Connect.createConnection();
        String query = "SELECT * FROM \"Rules\" WHERE survey_id = ?";
        PreparedStatement statement = null;
        List<Pair<RuleDTO, Integer>> rules = new LinkedList<>();;
        try {
            statement = Connect.conn.prepareStatement(query);
            statement.setString(1, surveyID);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                int surveyId = result.getInt("survey_id");
                String type = result.getString("type");
                String comparison = result.getString("comparison");
                int questionId = result.getInt("question_id");
                String answer = result.getString("answer");
                int id = result.getInt("id");
                List<RuleDTO> subRules = getSubRules(id);
                RuleDTO ruleDTO = new RuleDTO(subRules, RuleType.valueOf(type), Comparison.valueOf(comparison), questionId, parseList(Arrays.asList(answer.substring(1, answer.length() - 1).split(", "))));
                Pair <RuleDTO, Integer> toAdd = new Pair<>(ruleDTO, surveyId);
                rules.add(toAdd);
            }
            Connect.closeConnection();
        } catch (SQLException e) {e.printStackTrace();}
        return rules;
    }

    private List<Integer> parseList(List<String> stringList) {
        List<Integer> integerList = new LinkedList<>();

        for(String s: stringList)
            integerList.add(Integer.parseInt(s));

        return integerList;
    }

    private List<RuleDTO> getSubRules (int parent_id) {
        String query = "SELECT * FROM \"Rules\" WHERE parent_id = ?";
        PreparedStatement statement = null;
        List<RuleDTO> rules = new LinkedList<>();;
        try {
            statement = Connect.conn.prepareStatement(query);
            statement.setInt(1,parent_id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                String type = result.getString("type");
                String comparison = result.getString("comparison");
                int questionId = result.getInt("question_id");
                String answer = result.getString("answer");
                int id = result.getInt("id");
                List<RuleDTO> subRules = getSubRules(id);
                RuleDTO ruleDTO = new RuleDTO(subRules, RuleType.valueOf(type), Comparison.valueOf(comparison), questionId, parseList(Arrays.asList(answer.substring(1, answer.length() - 1).split(", "))));
                rules.add(ruleDTO);
            }
        } catch (SQLException e) {e.printStackTrace();}
        return rules;
    }

    //list of survey id's
    public List<String> getSurveyTitles(List<String> surveyIds) {
        List<String> titles = new LinkedList<>();
        Connect.createConnection();
        try {
            for (String title : surveyIds) { titles.addAll(getTitles(title)); }
            Connect.closeConnection();
        } catch (SQLException e) { e.printStackTrace(); }
        return titles;
    }

    //all titles of single survey id
    private List<String> getTitles (String surveyId) throws SQLException {
        String query = "SELECT * FROM \"Surveys\" WHERE id = ?";
        PreparedStatement statement = Connect.conn.prepareStatement(query);
        statement.setString(1, surveyId);
        ResultSet result = statement.executeQuery();
        List<String> titles = new LinkedList<>();

        while (result.next()) {
            String title = result.getString("title");
            titles.add(title);
        }
        return titles;
    }

    //===========================================================

    //========================== Answers ==========================

    public List<SurveyAnswersDTO> getAnswers(String surveyId) {
        Connect.createConnection();
        String query = "SELECT * FROM \"Answers\" WHERE survey_id = ?";
        PreparedStatement statement = null;

        List<SurveyAnswersDTO> output = new LinkedList<>();
        SurveyAnswersDTO surveyAnswersDTO;
        String symbol = "";
        try {
            statement = Connect.conn.prepareStatement(query);
            statement.setString(1, surveyId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                symbol = result.getString("school_symbol");
                String answer = result.getString("answer");
                String answerType = result.getString("answer_type");

                List<String> parsedAnswers = Arrays.asList(answer.split(","));
                List<String> answers = new LinkedList<>(parsedAnswers);

                List<String> parsedTypes = Arrays.asList(answerType.split(","));
                List<String> typesStrings = new LinkedList<>(parsedTypes);
                List<AnswerType> types = new LinkedList<>();
                for (String s : typesStrings) { types.add(AnswerType.valueOf(s)); };

                surveyAnswersDTO = new SurveyAnswersDTO(surveyId, answers, types);
                output.add(surveyAnswersDTO);
            }

            Connect.closeConnection();
        } catch (SQLException e) {e.printStackTrace();}

        return output;
    }

    public Map<String, List<SurveyAnswersDTO>> getAllAnswers() {
        //todo - almog
        return null;
    }

    //todo drop it later
    public List<SurveyAnswersDTO> getAnswerForSurvey(String surveyId) {
        return getAnswers(surveyId);
    }

    public void insertCoordinatorAnswers(String id, String symbol, List<String> answers, List<AnswerType> types) {
        Connect.createConnection();
        String sql = "INSERT INTO \"Answers\" (survey_id, school_symbol, answer, answer_type) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, symbol);
            preparedStatement.setString(3, ListToString(answers));
            preparedStatement.setString(4, ListATToString(types));
            preparedStatement.executeUpdate();

            Connect.closeConnection();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private String ListToString (List<String> list){ //output seperated by ,
        StringBuilder output = new StringBuilder();
        for (String s : list){
            output.append(s).append(",");
        }
        return output.substring(0,output.length()-1); //drop last ,
    }

    private String ListATToString (List<AnswerType> list){ //output seperated by ,
        StringBuilder output = new StringBuilder();
        for (AnswerType s : list){
            output.append(s).append(",");
        }
        return output.substring(0,output.length()-1); //drop last ,
    }

    // ==================== Cache Management =======================
    private void addSurveyToCache(String indexer, SurveyDTO survey){
        if(surveys.size() > cacheSize)
            removeLRU();

        surveys.put(indexer, new Pair<>(LocalDateTime.now() ,survey));
    }

    private void removeLRU(){
        LocalDateTime lastDate = LocalDateTime.now();
        String lastIndex = "";

        for(String index: surveys.keySet()){
            if(surveys.get(index).getFirst().isBefore(lastDate)){
                lastDate = surveys.get(index).getFirst();
                lastIndex = index;
            }
        }

        removeSurveyFromCache(lastIndex);
    }

    private Response<Boolean> removeSurveyFromCache(String index){
        if(!surveys.containsKey(index))
            return new Response<>(false, true, "survey with id " + index + " is not in cache");

        surveys.remove(index);
        return new Response<>(true, false, "survey with id " + index + " removed successfully");
    }

    // for testing purpose only
    public void clearCache(){
        surveys.clear();
//        answers.clear();
    }
}

//===========================================================
