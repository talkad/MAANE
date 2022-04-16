package Persistence;

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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class SurveyQueries {

    private static class CreateSafeThreadSingleton {
        private static final SurveyQueries INSTANCE = new SurveyQueries();
    }

    public static SurveyQueries getInstance() {
        return SurveyQueries.CreateSafeThreadSingleton.INSTANCE;
    }

    public String check(){return "hello";}

    //========================== Survey ==========================

    public Response<Boolean> insertSurvey(SurveyDTO surveyDTO) throws SQLException {
        Connect.createConnection();
        String sql = "INSERT INTO \"Surveys\" (id, title, description) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        preparedStatement.setString(1, surveyDTO.getId());
        preparedStatement.setString(2, surveyDTO.getTitle());
        preparedStatement.setString(3, surveyDTO.getDescription());
        int rows = preparedStatement.executeUpdate();
        insertQuestions(surveyDTO.getId(), surveyDTO.getQuestions());
        insertAnswers(surveyDTO.getId(), ListToString(surveyDTO.getAnswers()), surveyDTO.getTypes());
        Connect.closeConnection();
        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    private void insertQuestions (String surveyId, List<String> questions) throws SQLException {
        String sql = "INSERT INTO \"Questions\" (survey_id, index, question) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        for (String question : questions){
            preparedStatement.setString(1, surveyId);
            preparedStatement.setInt(2, questions.indexOf(question));
            preparedStatement.setString(3, question);
            preparedStatement.executeUpdate();
        }
    }

    public void insertAnswers (String surveyId, List<String> answers, List<AnswerType> answerTypes) throws SQLException {
        String sql = "INSERT INTO \"MultiChoices\" (survey_id, question_index, answer_type, choices) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
        for (String answer: answers) {
            preparedStatement.setString(1, surveyId);
            preparedStatement.setInt(2, answers.indexOf(answer));
            preparedStatement.setString(3, answerTypes.get(answers.indexOf(answer)).toString());
            preparedStatement.setString(4, answer);
            preparedStatement.executeUpdate();
        }
    }

    private List<String> ListToString(List<List<String>> l){
        List<String> result = new LinkedList<>();

        for(List<String> list: l){
            result.add(list.toString());
        }
        return result;
    }

    public Response<SurveyDTO> getSurvey(String id) throws SQLException {
        Connect.createConnection();
        String sqlSurvey = "SELECT * FROM \"Surveys\" WHERE id = ?";
        PreparedStatement statement = Connect.conn.prepareStatement(sqlSurvey);
        statement.setString(1, id);
        ResultSet resultSurvey = statement.executeQuery();
        SurveyDTO surveyDTO = new SurveyDTO();

        if(resultSurvey.next()) {
            surveyDTO.setId(resultSurvey.getString("id"));
            surveyDTO.setTitle(resultSurvey.getString("title"));
            surveyDTO.setDescription(resultSurvey.getString("description"));
            List<String> questions = getSurveyQuestions(id);
            List<List<String>> answers = getSurveyAnswers(id, questions);
            surveyDTO.setQuestions(questions);
            surveyDTO.setAnswers(answers);

            Connect.closeConnection();
        }
        else {
            Connect.closeConnection();
            return new Response<>(null, true, "failed to get survey");
        }

        return new Response<SurveyDTO>(surveyDTO, false, "survey loaded successfully");
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
        String sql = "INSERT INTO \"Rules\" (survey_id, goal_id, type, comparsion, question_id, answer) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, survey_id);
            preparedStatement.setInt(2, goalID);
            preparedStatement.setString(3, dto.getType().getType());
            preparedStatement.setString(4, dto.getComparison().getComparison());
            preparedStatement.setInt(5, dto.getQuestionID());
            preparedStatement.setInt(6, dto.getAnswer());
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
        String sql = "INSERT INTO \"Rules\" (survey_id, goal_id, type, comparsion, question_id, answer, parent_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            preparedStatement.setString(1, survey_id);
            preparedStatement.setInt(2, goalID);
            preparedStatement.setString(3, dto.getType().getType());
            preparedStatement.setString(4, dto.getComparison().getComparison());
            preparedStatement.setInt(5, dto.getQuestionID());
            preparedStatement.setInt(6, dto.getAnswer());
            preparedStatement.setInt(7, parent_id);
            preparedStatement.execute();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    //todo remove all subs too
    public Response<Boolean> removeRule (int ruleID) {
        Connect.createConnection();
        String sql = "DELETE FROM \"Rules\" WHERE id = ?";
        int rows = 0;
        try (PreparedStatement pstmt = Connect.conn.prepareStatement(sql)) {
            pstmt.setInt(1, ruleID);
            rows = pstmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");
    }

    //todo Integer is survey id
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
                int goalID = result.getInt("goal_id");
                String type = result.getString("type");
                String comparsion = result.getString("comparsion");
                int questionId = result.getInt("question_id");
                int answer = result.getInt("answer");
                int id = result.getInt("id");
                List<RuleDTO> subRules = getSubRules(id);
                RuleDTO ruleDTO = new RuleDTO(subRules, RuleType.valueOf(type), Comparison.valueOf(comparsion), questionId, answer);
                Pair <RuleDTO, Integer> toAdd = new Pair<>(ruleDTO, goalID);
                rules.add(toAdd);
            }
            Connect.closeConnection();
        } catch (SQLException e) {e.printStackTrace();}
        return rules;
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
                String comparsion = result.getString("comparsion");
                int questionId = result.getInt("question_id");
                int answer = result.getInt("answer");
                int id = result.getInt("id");
                List<RuleDTO> subRules = getSubRules(id);
                RuleDTO ruleDTO = new RuleDTO(subRules, RuleType.valueOf(type), Comparison.valueOf(comparsion), questionId, answer);
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

    //todo fix
    public List<SurveyAnswersDTO> getAnswers(String surveyId) {
        Connect.createConnection();
        String query = "SELECT * FROM \"Answers\" WHERE survey_id = ?";
        PreparedStatement statement = null;

        List<SurveyAnswersDTO> output = new LinkedList<>();
        SurveyAnswersDTO surveyAnswersDTO;
        List<String> answers = new LinkedList<>();
        List<AnswerType> answerTypes = new LinkedList<>();
        String symbol = "";
        try {
            statement = Connect.conn.prepareStatement(query);
            statement.setString(1, surveyId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                symbol = result.getString("school_symbol");
                String answer = result.getString("answer");
                String answerType = result.getString("answer_type");
                answers.add(answer);
                answerTypes.add(AnswerType.valueOf(answerType));
            }
            surveyAnswersDTO = new SurveyAnswersDTO(surveyId, symbol, answers, answerTypes);
            output.add(surveyAnswersDTO);

            Connect.closeConnection();
        } catch (SQLException e) {e.printStackTrace();}

        return output;
    }

    public Map<String, List<SurveyAnswersDTO>> getAllAnswers() {
        //todo - almog
        return null;
    }

    public List<SurveyAnswersDTO> getAnswerForSurvey(String surveyId) {
        //todo - almog
        return null;
    }
}

    //===========================================================

// --- Old one ---
//    public static void insertAnswers (String surveyId, List<List<String>> answers, List<AnswerType> answerTypes) throws SQLException {
//        String sql = "INSERT INTO \"Answers\" (survey_id, question_index, answer_index, answer_type, answer) VALUES (?, ?, ?, ?, ?)";
//        PreparedStatement preparedStatement = Connect.conn.prepareStatement(sql);
//        for (List<String> answersList : answers){
//            for (String answer: answersList) {
//                preparedStatement.setString(1, surveyId);
//                preparedStatement.setInt(2, answers.indexOf(answersList));
//                preparedStatement.setInt(3, answersList.indexOf(answer));
//                preparedStatement.setString(4, answerTypes.get(answers.indexOf(answersList)).toString());
//                preparedStatement.setString(5, answer);
//                preparedStatement.executeUpdate();
//            }
//        }
//    }





//    private int getParentId (String survey_id, List<String> questions) throws SQLException {
//        String query = "SELECT * FROM \"Answers\" WHERE survey_id = ?";
//        PreparedStatement statement = Connect.conn.prepareStatement(query);
//        statement.setString(1, survey_id);
//        ResultSet result = statement.executeQuery(query);
//        List<List<String>> answers = new LinkedList<>();
//
//        while (result.next()) {
//            // int index = result.getInt("index");
//            String question = result.getString("question");
//            questions.add(question);
//        }
//        return 2;
//    }
