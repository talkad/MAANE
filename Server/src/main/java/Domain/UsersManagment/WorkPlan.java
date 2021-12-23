package Domain.UsersManagment;

import java.time.LocalDate;
import java.util.*;

public class WorkPlan {
    String username;
    Map <String, String> calendar;

    public WorkPlan(String username, int year){
        this.username = username;
        this.calendar = GenerateCalendarForYear(year);
    }

    public void insertActivity (String date, String activity){
        calendar.put(date, activity);
    }

    public void printMe (){
        //ArrayList<String> keySet = calendar.keySet().toArray();
        for (String key: calendar.keySet()) {
            System.out.println("date: " + key + " activity: " + calendar.get(key));
        }
    }

    private static List<String> generateDatesForYear (int year){
        String s = year + "-01-01";
        String e = year + "-12-31";
        LocalDate start = LocalDate.parse(s);
        LocalDate end = LocalDate.parse(e);
        List<String> totalDates = new ArrayList<>();
        while (!start.isAfter(end)) {
            totalDates.add(start.toString());
            start = start.plusDays(1);
        }
        return totalDates;
    }

    private Map<String, String> GenerateCalendarForYear (int year){
        List <String> datesFor2021 = generateDatesForYear(year);
        Map<String, String> calendar = new TreeMap<>();
        for (String day: datesFor2021) {
            calendar.put(day,"");
        }
        return calendar;
    }



}



//        Calendar calendar = Calendar.getInstance();
//        Date date = new Date("11/12/21"); //month first
//        calendar.setTime(date);
//        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//        System.out.println(dayOfWeek);