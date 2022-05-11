package Persistence;

import Communication.DTOs.ActivityDTO;
import Communication.DTOs.WorkPlanDTO;
import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Repository
public class WorkPlanQueries {
    private static class CreateSafeThreadSingleton {
        private static final WorkPlanQueries INSTANCE = new WorkPlanQueries();
    }

    public static WorkPlanQueries getInstance() {
        return WorkPlanQueries.CreateSafeThreadSingleton.INSTANCE;
    }

    public Response<Boolean> insertUserWorkPlan(String username , Map<String, WorkPlanDTO> workPlan){
        Connect.createConnection();
        int rows = 0;
        String sql = "INSERT INTO \"WorkPlans\" (username, year, date, activities) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Connect.conn.prepareStatement(sql);
            for (Map.Entry<String,WorkPlanDTO> entry : workPlan.entrySet()){
                String year =  entry.getKey();
                WorkPlanDTO workPlanDTO = entry.getValue();
                for (Pair<String, List<ActivityDTO>> annualPlan : workPlanDTO.getCalendar()) {
                    String date = annualPlan.getFirst();
                    String activities = ActivitiesToString(annualPlan.getSecond());

                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, year);
                    preparedStatement.setString(3, date);
                    preparedStatement.setString(4, activities);
                }
            }

            rows = preparedStatement.executeUpdate();
            Connect.closeConnection();
        } catch (SQLException e) {e.printStackTrace();}

        return rows>0 ? new Response<>(true, false, "") :
                new Response<>(false, true, "bad Db writing");

    }

    /***
     *
     * @param activities
     * @return String in the shape of ActivityDto_1 | ActivityDto_2 | ActivityDto_3 ....
     * while ActivityDto in the shape of school_id ^ title
     */
    private String ActivitiesToString (List<ActivityDTO> activities){
        StringBuilder output = new StringBuilder();
        for (ActivityDTO activity : activities){
            output.append(activity.getSchoolId()).append(" ^ ").append(activity.getTitle()).append(" | ");
        }
        return output.substring(0, output.length()-3);
    }


    public Response<Map<String, WorkPlanDTO>> getUserWorkPlan(String username, String year)  {
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
    }

    /***
     *
     * @param activities string
     * @return parse back
     */
    private List<ActivityDTO> StringToActivities (String activities){
        List<ActivityDTO> activityDTOS = new LinkedList<>();
        String [] activitiesArray = activities.split(" | ");
        for (String activity : activitiesArray){
            String [] details = activity.split(" ^ ");
            String schoolId = details[0];
            String title = details[1];
            ActivityDTO activityDTO = new ActivityDTO(schoolId, title);
            activityDTOS.add(activityDTO);
        }
        return activityDTOS;
    }

}
