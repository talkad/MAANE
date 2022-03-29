package Domain.UsersManagment;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.WorkPlan.Goal;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents the annual schedule of some user
 * it builds and holds the calendar with all dates and their activities
 *
 */

public class WorkPlan {
    protected TreeMap <String, List<Activity>> calendar; //Date and his activities for each day of year

    public WorkPlan(int year){
        this.calendar = GenerateCalendarForYear(year);
    }

    public TreeMap<String, List<Activity>> getCalendar (){
        return calendar;
    }

    /*
    Inserts 2 goals from 2 schools for same date
     */
    public Response<Boolean> insertActivityToFirstAvailableDate (Pair<String, Goal> input1, Pair<String, Goal> input2){
        Activity firstActivity = new Activity(input1.getFirst(), input1.getSecond().getTitle());
        Activity secondActivity = new Activity(input2.getFirst(), input2.getSecond().getTitle());
        String freeDate = findDate();

        if (freeDate.equals("")) //no free date
            return new Response<>(false, true, "no free days");

        insertActivity (freeDate, firstActivity);
        insertActivity (freeDate, secondActivity);
        return new Response<>(false, false, "Success");
    }

    /*
    Gets a pair of <School name, Goal> and insert it to the first available date (not friday/saturday)
     */
    public Response<Boolean> insertActivityToFirstAvailableDate (Pair<String, Goal> input){
        Activity activity = new Activity(input.getFirst(), input.getSecond().getTitle());
        String freeDate = findDate();

        if (freeDate.equals("")) //no free date
            return new Response<>(false, true, "no free days");

        insertActivity (freeDate, activity);
        return new Response<>(false, false, "Success");
    }

    private void insertActivity (String date, Activity activity){
        calendar.get(date).add(activity);
    }

    // find the next avaliable date
    private String findDate (){
        String freeDate = "";
        for (String date: calendar.descendingKeySet()) {
            if (calendar.get(date).isEmpty()) {
                if (dayIsFridayOrSaturday(date))
                    continue;
                freeDate = date;
            }
        }
        return freeDate;
    }

    public void printMe (){
        for (String key: calendar.keySet()) {
            System.out.println("Date: " + key);
            for (Activity activity: calendar.get(key))
                System.out.println("==> activity " + activity.getTitle() + " scheduled for school " + activity.getSchool());
        }
    }

    //returns the schedule rom month {input} till the end of year
    public Map<String, List<Activity>> getScheduleFromMonth (String input){
        final String month = addZeroIfNeeded(input); //need to work with "01","02" etc
        Map <String, List<Activity>> output = new TreeMap<>(calendar);
        int intMonth = Integer.parseInt(month);
        if (intMonth >= 9){
            for (int i=9; i <= intMonth; i++){
                final String finalI = addZeroIfNeeded(i+"");
                output.entrySet().removeIf(e -> whatMonthIsIt(e.getKey()).equals(finalI));
            }
        }
        else {
            for (int i=1; i <= intMonth; i++){
                final String finalI = addZeroIfNeeded(i+"");
                output.entrySet().removeIf(e -> whatMonthIsIt(e.getKey()).equals(finalI));
            }
            for (int i=9; i <= 12; i++){
                final String finalI = addZeroIfNeeded(i+"");
                output.entrySet().removeIf(e -> whatMonthIsIt(e.getKey()).equals(finalI));
            }
        }
        return output;
    }

    private String addZeroIfNeeded (String number){
        return number.length() == 1 ? "0" + number : number;
    }

    //Generate the dates
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

    private TreeMap<String, List<Activity>> GenerateCalendarForYear (int year){
        List <String> datesForYear = generateDatesForYear(year);
        calendar = new TreeMap<>();
        for (String day: datesForYear) {
            calendar.put(day, new LinkedList<>());
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

    public String whatMonthIsIt (String date){
        String [] arr = date.split("-"); //[month,day,year]
        return ""+arr[1];
    }
}

//String todayDate = java.time.LocalDate.now().toString(); //example 2021-12-23