package Domain.WorkPlan;


import Communication.DTOs.GoalDTO;
import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GoalsManagement {
    private Map<String, Map<String, List<Goal>>> goals;
    private AtomicInteger goalId;

    private GoalsManagement() {
        this.goals = new ConcurrentHashMap<>();
        this.goalId = new AtomicInteger(0);
    }

    private static class CreateSafeThreadSingleton {
        private static final GoalsManagement INSTANCE = new GoalsManagement();
    }

    public static GoalsManagement getInstance() {
        return GoalsManagement.CreateSafeThreadSingleton.INSTANCE;
    }

    public void addGoalsField(String workField) {
        if(!goals.containsKey(workField)) {
            goals.put(workField, new ConcurrentHashMap<>());
        }
    }

    public Response<Boolean> addGoalToField(String workField, GoalDTO goalDTO, String year){
        if(this.goals.containsKey(workField)){
            Goal goal;
            if(!this.goals.get(workField).containsKey(year)){
                this.goals.get(workField).put(year, new Vector<>());
            }
            goalDTO.setGoalId(this.goalId.getAndIncrement());
            goal = new Goal(goalDTO);
            this.goals.get(workField).get(year).add(goal);//todo check errors on year
            return new Response<>(true, false, "successfully added goals to the work field: " + workField);
        }
        return new Response<>(false, true, "work field: " + workField + " does not exists");
    }

    public Response<List<Goal>> getGoals(String workField, String year){
        if(this.goals.containsKey(workField) && this.goals.get(workField).containsKey(year)){
            return new Response<>(this.goals.get(workField).get(year), false, "successfully acquired goals from the work field: " + workField);
        }
        return new Response<>(null, true, "work field: " + workField + " does not exists"); //todo cant really get here
    }

    public Response<List<GoalDTO>> getGoalsDTO(String workField, String year){
        if(this.goals.containsKey(workField) && this.goals.get(workField).containsKey(year)){
            List<GoalDTO> goalDTOList = new Vector<>();
            for (Goal g: this.goals.get(workField).get(year)) {
                GoalDTO goalDTO = new GoalDTO();
                goalDTO.setGoalId(g.getGoalId());
                goalDTO.setDescription(g.getDescription());
                goalDTO.setQuarterly(g.getQuarterly());
                goalDTO.setTitle(g.getTitle());
                goalDTO.setWeight(g.getWeight());
                goalDTOList.add(goalDTO);
            }
            return new Response<>(goalDTOList, false, "successfully acquired goals from the work field: " + workField);
        }
        return new Response<>(null, true, "work field: " + workField + " does not exists"); //todo cant really get here
    }

    public Response<Goal> getGoalByTitle(String workField,String title, String year){
        if(this.goals.containsKey(workField) && this.goals.get(workField).containsKey(year)){
            for (Goal goal: this.goals.get(workField).get(year)) {
                if(goal.getTitle().equals(title)){
                    return new Response<>(goal, false, "goal found");
                }
            }
        }
        return new Response<>(null, true, "no such goal exists");
    }

    public Response<List<Goal>> getGoalsById(String workField, List<Integer> goalsId, String year){
        List<Goal> goalsList = new Vector<>();
        boolean idNotFound;
        if(this.goals.containsKey(workField) && this.goals.get(workField).containsKey(year)){
            if(goalsId != null && goalsId.size() > 0) {
                for (Integer goalId: goalsId) {
                    idNotFound = true;
                    for (Goal goal : this.goals.get(workField).get(year)) {
                        if (goal.getGoalId() == goalId) {
                            goalsList.add(goal);
                            idNotFound = false;
                            break;
                        }
                    }
                    if(idNotFound){
                        return new Response<>(null, true, "one of the provided goals doesn't exist");
                    }
                }
                return new Response<>(goalsList, false, "all goals found");
            }//todo maybe add else and error
        }
        return new Response<>(null, true, "no such goal exists");
    }

    public Response<Boolean> removeGoal(String workField, String year, int goalId){
        if(goals.containsKey(workField) && goals.get(workField).containsKey(year)){
            int goalIndex = -1;
            for(Goal g: goals.get(workField).get(year)){
                if(g.getGoalId() == goalId){
                    goalIndex = goals.get(workField).get(year).indexOf(g);
                    break;
                }
            }
            if(goalIndex > -1){
                goals.get(workField).get(year).remove(goalIndex);
                return new Response<>(true, false, "successfully removed goal");
            }
        }
        return new Response<>(false, true, "failed to remove goal");
    }

    public void clearGoals(){
        this.goals = new ConcurrentHashMap<>();
    }
}
