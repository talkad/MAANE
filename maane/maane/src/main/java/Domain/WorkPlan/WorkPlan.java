package Domain.WorkPlan;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.Activity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the annual schedule of some user
 * it builds and holds the calendar with all dates and their activities
 *
 */

public class WorkPlan {
    protected TreeMap<LocalDateTime, Activity> calendar; //Date and his activities for each day of year
    protected int year;
    protected LocalDateTime currDateToInsert;

    public WorkPlan(int year) {
        this.calendar = GenerateCalendarForYear(year);
        this.year = year;
        this.currDateToInsert = LocalDateTime.of(year, 9, 1, 0, 0);
        checkFridaySaturday();
    }

    public TreeMap<LocalDateTime, Activity> getCalendar() {
        return this.calendar;
    }

    /*
    Inserts 2 goals from 2 schools for same date
     */
    public Response<Boolean> insertActivityToFirstAvailableDate(Pair<String, Goal> input1, Pair<String, Goal> input2) {
        Activity firstActivity = new Activity(input1.getFirst(), input1.getSecond().getTitle());
        Activity secondActivity = new Activity(input2.getFirst(), input2.getSecond().getTitle());//todo isnt it goald id?

        LocalDateTime freeDate = findDate();
        if (freeDate == null) //no free date
            return new Response<>(false, true, "no free days");//todo not actually failed check its ok
        insertActivity(freeDate, firstActivity);

        LocalDateTime freeDate2 = findDate();
        if (freeDate2 == null) //no free date
            return new Response<>(false, true, "no free days");//todo not actually failed check its ok
        insertActivity(freeDate2, secondActivity);

        return new Response<>(false, false, "Success");
    }

    /*
    Gets a pair of <School name, Goal> and insert it to the first available date (not friday/saturday)
     */
    public Response<Boolean> insertActivityToFirstAvailableDate(Pair<String, Goal> input) {
        Activity activity = new Activity(input.getFirst(), input.getSecond().getTitle());
        LocalDateTime freeDate = findDate();

        if (freeDate == null) //no free date
            return new Response<>(false, true, "no free days");//todo not actually failed check its ok

        insertActivity(freeDate, activity);
        return new Response<>(false, false, "Success");
    }

    private void insertActivity(LocalDateTime date, Activity activity) {
        this.calendar.put(date, activity);
    }

    // find the next avaliable date
    private LocalDateTime findDate() {
        //LocalDateTime localDateTime = calendar.keySet().iterator().next();
        LocalDateTime freeDate = null;

        for (LocalDateTime date : calendar.keySet()) {

            if (calendar.get(date) == null) {
                boolean isFridayOrSaturday = date.getDayOfWeek() == DayOfWeek.FRIDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY;
                if (!isFridayOrSaturday) {
                    freeDate = date;
                    break;
                }
            }
        }
        return freeDate;
    }

    public void printMe() {
        for (LocalDateTime key : calendar.keySet()) {
            System.out.println("Date: " + key);
            if(calendar.get(key) != null){
                System.out.println("==> activity " + calendar.get(key).getTitle() + " scheduled for school " + calendar.get(key).getSchool());
            }
        }
    }

    //returns the schedule from month {input} till the end of year
    public Map<LocalDateTime, List<Activity>> getScheduleFromMonth(String input) {
        return null; //todo eh?
//        final String month = addZeroIfNeeded(input); //need to work with "01","02" etc
//        Map <LocalDateTime, List<Activity>> output = new TreeMap<>();


//        int intMonth = Integer.parseInt(month);
//        if (intMonth >= 9){
//            for (int i=9; i <= intMonth; i++){
//                final String finalI = addZeroIfNeeded(i+"");
//                output.entrySet().removeIf(e -> whatMonthIsIt(e.getKey()).equals(finalI));
//            }
//        }
//        else {
//            for (int i=1; i <= intMonth; i++){
//                final String finalI = addZeroIfNeeded(i+"");
//                output.entrySet().removeIf(e -> whatMonthIsIt(e.getKey()).equals(finalI));
//            }
//            for (int i=9; i <= 12; i++){
//                final String finalI = addZeroIfNeeded(i+"");
//                output.entrySet().removeIf(e -> whatMonthIsIt(e.getKey()).equals(finalI));
//            }
//        }
//        return output;
    }

    private String addZeroIfNeeded(String number) {
        return number.length() == 1 ? "0" + number : number;
    }

    //Generate the dates
    private static List<LocalDateTime> generateDatesForYear(int year) {

        LocalDate startDate = LocalDate.of(year, 9, 1);
        LocalDate endDate = LocalDate.of(year + 1, 6, 21);

        List<LocalDate> localDates = startDate.datesUntil(endDate).collect(Collectors.toList());
        List<LocalDateTime> localDateTimes = new Vector<>();
        for (LocalDate localDate : localDates) {
            localDateTimes.add(localDate.atStartOfDay());
            localDateTimes.add(localDate.atStartOfDay().plusHours(1));

        }
        return localDateTimes;

    }

    private TreeMap<LocalDateTime, Activity> GenerateCalendarForYear(int year) {
        List<LocalDateTime> datesForYear = generateDatesForYear(year);
        calendar = new TreeMap<>();
        for (LocalDateTime day : datesForYear) {
            calendar.put(day, null);
        }
        return calendar;
    }


//    public Response<Boolean> insertActivityEveryWeek(Pair<String, Goal> input) {
//        Activity activity = new Activity(input.getFirst(), input.getSecond().getTitle());
//        LocalDateTime freeDate = findDate();
//
//        if (freeDate == null) //no free date
//            return new Response<>(false, true, "no free days");//todo not actually failed check its ok
//
//        LocalDateTime lastDay = LocalDateTime.of(this.year + 1, 6, 21, 0, 0);
//        while (freeDate.isBefore(lastDay)) {
//            insertActivity(freeDate, activity);
//            freeDate = freeDate.plusWeeks(1);
//        }
//
//        return new Response<>(false, false, "Success");
//    }
//
//    public Response<Boolean> insertActivityEveryWeek(Pair<String, Goal> input1, Pair<String, Goal> input2) {
//        Activity firstActivity = new Activity(input1.getFirst(), input1.getSecond().getTitle());
//        Activity secondActivity = new Activity(input2.getFirst(), input2.getSecond().getTitle());
//        LocalDateTime lastDay = LocalDateTime.of(this.year + 1, 6, 21, 0, 0);
//
//        LocalDateTime freeDate1 = findDate();
//        if (freeDate1 == null) //no free date
//            return new Response<>(false, true, "no free days");//todo not actually failed check its ok
//
//        while (freeDate1.isBefore(lastDay)){
//            insertActivity(freeDate1, firstActivity);
//            freeDate1 = freeDate1.plusWeeks(1);
//        }
//
//        LocalDateTime freeDate2 = findDate();
//        if (freeDate2 == null) //no free date
//            return new Response<>(false, true, "no free days");//todo not actually failed check its ok
//        freeDate2 = freeDate2.toLocalDate().atStartOfDay().plusHours(1);
//
//        while (freeDate2.isBefore(lastDay)){
//            insertActivity(freeDate2, secondActivity);
//            freeDate2 = freeDate2.plusWeeks(1);
//        }
//
//        return new Response<>(false, false, "Success");
//    }


    public Response<Boolean> insertActivityEveryWeek(Pair<String, Goal> input) {
        Activity activity = new Activity(input.getFirst(), input.getSecond().getTitle());
        LocalDateTime lastDay = LocalDateTime.of(this.year + 1, 6, 21, 0, 0);

        if (lastDay.isBefore(currDateToInsert)) //no free date
            return new Response<>(false, true, "no free days");//todo not actually failed check its ok

        insertActivity(currDateToInsert, activity);
        currDateToInsert = currDateToInsert.plusWeeks(1);

        return new Response<>(false, false, "Success");
    }

    public Response<Boolean> insertActivityEveryWeek(Pair<String, Goal> input1, Pair<String, Goal> input2) {
        Activity firstActivity = new Activity(input1.getFirst(), input1.getSecond().getTitle());
        Activity secondActivity = new Activity(input2.getFirst(), input2.getSecond().getTitle());
        LocalDateTime lastDay = LocalDateTime.of(this.year + 1, 6, 21, 0, 0);

        if (lastDay.isBefore(currDateToInsert)) //no free date
            return new Response<>(false, true, "no free days");//todo not actually failed check its ok

        insertActivity(currDateToInsert, firstActivity);
        insertActivity(currDateToInsert.toLocalDate().atStartOfDay().plusHours(1), secondActivity);
        currDateToInsert = currDateToInsert.plusWeeks(1);

        return new Response<>(false, false, "Success");
    }

    private void checkFridaySaturday(){
        if(currDateToInsert.getDayOfWeek() == DayOfWeek.FRIDAY)
            currDateToInsert = currDateToInsert.plusDays(2);
        if(currDateToInsert.getDayOfWeek() == DayOfWeek.SATURDAY)
            currDateToInsert = currDateToInsert.plusDays(1);
    }
}