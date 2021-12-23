package Domain.UsersManagment;

import Domain.CommonClasses.Pair;
import Domain.WorkPlan.Goal;

import java.time.LocalDate;
import java.util.*;

public class WorkPlan {
    TreeMap <String, String> calendar;

    public WorkPlan(int year){
        this.calendar = GenerateCalendarForYear(year);
    }

    //todo: check
    public boolean insertActivityToFirstAvailableDate (Pair<String, Goal> input1, Pair<String, Goal> input2){
        return insertActivityToFirstAvailableDate (input1) & insertActivityToFirstAvailableDate (input2);
    }

    public boolean insertActivityToFirstAvailableDate (Pair<String, Goal> input){
        //String todayDate = java.time.LocalDate.now().toString(); //example 2021-12-23
        String activity = input.getSecond().getDescription();
        String freeDate = "";

        for (String date: calendar.descendingKeySet()) {
            if (calendar.get(date).equals("")) {
                if (dayIsFridayOrSaturday(date))
                    continue;
                freeDate = date;
            }
        }

        if (freeDate.equals("")) //no free date
            return false;

        insertActivity (freeDate, activity);
        return true;
    }

    private void insertActivity (String date, String activity){
        calendar.put(date, activity);
    }

    public void printMe (){
        for (String key: calendar.keySet()) {
            System.out.println("date: " + key + " activity: " + calendar.get(key));
        }
    }

    private static List<String> generateDatesForYear (int year){
        String s = year + "-09-01";
        String e = year + "-12-31";
        LocalDate start = LocalDate.parse(s);
        LocalDate end = LocalDate.parse(e);
        List<String> totalDates = new ArrayList<>();
        while (!start.isAfter(end)) {
            totalDates.add(start.toString());
            start = start.plusDays(1);
        }

        s = (year + 1) + "-01-01";
        e = (year + 1) + "-06-20";
        start = LocalDate.parse(s);
        end = LocalDate.parse(e);
        while (!start.isAfter(end)) {
            totalDates.add(start.toString());
            start = start.plusDays(1);
        }

        return totalDates;
    }

    private TreeMap<String, String> GenerateCalendarForYear (int year){
        List <String> datesForYear = generateDatesForYear(year);
        calendar = new TreeMap<>();
        for (String day: datesForYear) {
            calendar.put(day,"");
        }
        return calendar;
    }

    private boolean dayIsFridayOrSaturday (String date){
        String [] arr = date.split("-"); //[month,day,year]
        String year = arr[0];
        String month = arr[1];
        String day = arr[2];
        String weekDay = whatDayIsIt(month+"/"+day+"/"+year.substring(1));
        return (weekDay.equals("7") | weekDay.equals("6"));
    }

    private String whatDayIsIt (String date){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        Date myDate = new Date(date); //month first
        calendar.setTime(myDate);
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        return ""+dayOfWeek;
    }
}
