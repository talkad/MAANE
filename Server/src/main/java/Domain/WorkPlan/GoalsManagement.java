package Domain.WorkPlan;


import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class GoalsManagement {
    private Map<String, List<Goal>> goals;

    private GoalsManagement() {
        this.goals = new ConcurrentHashMap<>();
    }

    private static class CreateSafeThreadSingleton {
        private static final GoalsManagement INSTANCE = new GoalsManagement();
    }

    public static GoalsManagement getInstance() {
        return GoalsManagement.CreateSafeThreadSingleton.INSTANCE;
    }

    public void addGoalsField(String workField) {//todo maybe change name
        goals.put(workField, new Vector<>());
    }

    public Response<Boolean> addGoalsToField(String workField, List<Goal> goalList){//todo bring goalId into account
        if(this.goals.containsKey(workField)){
            this.goals.get(workField).addAll(goalList);
            return new Response<>(true, false, "successfully added goals to the work field: " + workField);
        }
        return new Response<>(false, true, "work field: " + workField + " does not exists");
    }

    public Response<List<Goal>> getGoals(String workField){
        if(this.goals.containsKey(workField)){
            return new Response<>(this.goals.get(workField), false, "successfully acquired goals from the work field: " + workField);
        }
        return new Response<>(null, true, "work field: " + workField + " does not exists"); //todo cant really get here and make sure null not a problem
    }
}
