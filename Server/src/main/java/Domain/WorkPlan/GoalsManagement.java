package Domain.WorkPlan;


import Domain.CommonClasses.Response;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class GoalsManagement {
    private Map<String, Map<String, List<Goal>>> goals;

    private GoalsManagement() {
        this.goals = new ConcurrentHashMap<>();
    }

    private static class CreateSafeThreadSingleton {
        private static final GoalsManagement INSTANCE = new GoalsManagement();
    }

    public static GoalsManagement getInstance() {
        return GoalsManagement.CreateSafeThreadSingleton.INSTANCE;
    }

    public void addGoalsField(String workField) {
        goals.put(workField, new ConcurrentHashMap<>());//todo init hashmap with year
    }

    public Response<Boolean> addGoalsToField(String workField, List<Goal> goalList, String year){//todo bring goalId into account
        if(this.goals.containsKey(workField)){
            if(!this.goals.get(workField).containsKey(year)){
                this.goals.get(workField).put(year, new Vector<>());
            }
            this.goals.get(workField).get(year).addAll(goalList);//todo check errors on year
            return new Response<>(true, false, "successfully added goals to the work field: " + workField);
        }
        return new Response<>(false, true, "work field: " + workField + " does not exists");
    }

    public Response<List<Goal>> getGoals(String workField, String year){
        if(this.goals.containsKey(workField)){//todo add check for year
            return new Response<>(this.goals.get(workField).get(year), false, "successfully acquired goals from the work field: " + workField);
        }
        return new Response<>(null, true, "work field: " + workField + " does not exists"); //todo cant really get here and make sure null not a problem
    }

    public Response<Goal> getGoalByTitle(String workField,String title, String year){
        if(this.goals.containsKey(workField)){
            for (Goal goal: this.goals.get(workField).get(year)) {//todo check for errors on year
                if(goal.getTitle().equals(title)){
                    return new Response<>(goal, false, "goal found");
                }
            }
        }
        return new Response<>(null, true, "no such goal exists");
    }

    public Response<List<Goal>> getGoalsByTitles(String workField, List<String> titles, String year){
        List<Goal> goalsList = new Vector<>();
        boolean titleNotFound;
        if(this.goals.containsKey(workField)){
            if(titles != null && titles.size() > 0) {
                for (String title: titles) {
                    titleNotFound = true;
                    for (Goal goal : this.goals.get(workField).get(year)) {//todo check errors on year
                        if (goal.getTitle().equals(title)) {
                            goalsList.add(goal);
                            titleNotFound = false;
                            break;
                        }
                    }
                    if(titleNotFound){
                        return new Response<>(null, true, "one of the provided goals doesn't exist");
                    }
                }
                return new Response<>(goalsList, false, "all titles found");
            }//todo maybe add else and error
        }
        return new Response<>(null, true, "no such goal exists");
    }

    public void clearGoals(){
        this.goals = new ConcurrentHashMap<>();
    }
}
