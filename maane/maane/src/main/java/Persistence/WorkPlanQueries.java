package Persistence;

import Communication.DTOs.ActivityDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

@Repository
public class WorkPlanQueries {
    private static class CreateSafeThreadSingleton {
        private static final WorkPlanQueries INSTANCE = new WorkPlanQueries();
    }

    public static WorkPlanQueries getInstance() {
        return WorkPlanQueries.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<Boolean> insertUserWorkPlan(String username, WorkPlanDTO workPlan, Integer year){
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"WorkPlans\" (username, year, date, activities) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            for (Pair<LocalDateTime, ActivityDTO> annualPlan : workPlan.getCalendar()) {
                if(annualPlan.getSecond() != null) {
                    LocalDateTime date = annualPlan.getFirst();
                    String activities = ActivitiesToString(annualPlan.getSecond());
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, year);
                    preparedStatement.setTimestamp(3, Timestamp.valueOf(date));
                    preparedStatement.setString(4, activities);
                    rows = preparedStatement.executeUpdate();
                }
            }

            Connect.closeConnection();
        } catch (SQLException e) {e.printStackTrace();}

        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");

    }

    /***
     *
     * @param activity
     * @return String in the shape of ActivityDto_1 | ActivityDto_2 | ActivityDto_3 ....
     * while ActivityDto in the shape of school_id title
     */
    private String ActivitiesToString (ActivityDTO activity){
        if (activity == null) return "";
        return activity.getSchoolId() + " " + activity.getGoalId().toString() + " " + activity.getTitle();
    }

    /*public Response<Map<String, WorkPlanDTO>> getUserWorkPlan(String username)  {
        Connect.createConnection();
        String sql = "SELECT * FROM \"WorkPlans\" WHERE username = ? AND year = ?";
        PreparedStatement statement;
        Map<String, WorkPlanDTO> output = new TreeMap<>();
        try {
            statement = Connect.conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, year);
            ResultSet result = statement.executeQuery();

            List<Pair<String, List<ActivityDTO>>> calendar = new LinkedList<>();
            while(result.next()) {
                String date = result.getString("date");
                String activities = result.getString("activities");
                List<ActivityDTO> activityDTOS = StringToActivities(activities);
                Pair<String, List<ActivityDTO>> toAdd = new Pair<>(date, activityDTOS);
                calendar.add(toAdd);
            }
            output.put(year, new WorkPlanDTO(calendar));

            Connect.closeConnection();
            return new Response<>(output, false, "successfully got work plans");
        } catch (SQLException throwables) {throwables.printStackTrace();}
        return new Response<>(null, true, "failed to get work plans");
    }*/

    public Response<WorkPlanDTO> getUserWorkPlanByYear(String username, Integer year)  {
        Connect.createConnection();
        String sql = "SELECT * FROM \"WorkPlans\" WHERE username = ? AND year = ?";
        PreparedStatement statement;
        WorkPlanDTO workPlanDTO;
        try {
            statement = Connect.conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setInt(2, year);
            ResultSet result = statement.executeQuery();

            List<Pair<LocalDateTime, ActivityDTO>> calendar = new LinkedList<>();
            while(result.next()) {
                LocalDateTime date = result.getTimestamp("date").toInstant().atZone(TimeZone.getTimeZone("Asia/Jerusalem").toZoneId()).toLocalDateTime();

                String activities = result.getString("activities");
                ActivityDTO activityDTO = StringToActivities(activities);
                Pair<LocalDateTime, ActivityDTO> toAdd = new Pair<>(date, activityDTO);
                calendar.add(toAdd);
            }
            workPlanDTO = new WorkPlanDTO(calendar);

            Connect.closeConnection();
            return new Response<>(workPlanDTO, false, "successfully got work plans");
        } catch (SQLException throwables) {throwables.printStackTrace();}
        return new Response<>(null, true, "failed to get work plans");
    }

    public Response<WorkPlanDTO> getUserWorkPlanByYearAndMonth(String username, Integer year, Integer month)  {
        Connect.createConnection();
        String sql = "SELECT * FROM \"WorkPlans\" WHERE username = ? AND (year = ? AND EXTRACT(MONTH FROM date) = ?)";
        PreparedStatement statement;
        WorkPlanDTO workPlanDTO;
        try {
            statement = Connect.conn.prepareStatement(sql);
            statement.setString(1, username);
            statement.setInt(2, year);
            statement.setInt(3, month);

            ResultSet result = statement.executeQuery();

            List<Pair<LocalDateTime, ActivityDTO>> calendar = new LinkedList<>();
            while(result.next()) {
                LocalDateTime date = result.getTimestamp("date").toInstant().atZone(TimeZone.getTimeZone("Asia/Jerusalem").toZoneId()).toLocalDateTime();

                String activities = result.getString("activities");
                ActivityDTO activityDTO = StringToActivities(activities);
                Pair<LocalDateTime, ActivityDTO> dateAndActivity = new Pair<>(date, activityDTO);
                calendar.add(dateAndActivity);
            }
            workPlanDTO = new WorkPlanDTO(calendar);

            Connect.closeConnection();
            return new Response<>(workPlanDTO, false, "successfully got work plans");
        } catch (SQLException throwables) {throwables.printStackTrace();}
        return new Response<>(null, true, "failed to get work plans");
    }

    /***
     *
     * @param activity string
     * @return parse back
     */
    private ActivityDTO StringToActivities (String activity){
        if(activity == null || activity.equals("")) return new ActivityDTO("", -1, "");//todo problem
        String [] activitiesArray = activity.split(" ", 3);
        String schoolId = activitiesArray[0];
        Integer goalId = Integer.parseInt(activitiesArray[1]);
        String title = activitiesArray[2];
        return new ActivityDTO(schoolId, goalId, title);
    }
}