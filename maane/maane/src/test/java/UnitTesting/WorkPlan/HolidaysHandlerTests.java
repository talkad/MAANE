package UnitTesting.WorkPlan;
import Communication.Initializer.ServerContextInitializer;
import Domain.WorkPlan.HolidaysHandler;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class HolidaysHandlerTests {

    private HolidaysHandler holidaysHandler;

    @Before
    public void setup(){
        ServerContextInitializer.getInstance().setMockMode();
        holidaysHandler = new HolidaysHandler(2022);
    }

//    @Test
//    public void checkInsert() {
//        ArrayList<String[]> holidays = holidaysHandler.getHolidaysForYear(2022);
//        for (String[] entry : holidays) {
//            System.out.println(entry[0] + " on date: " + entry[1] + " of year " + entry[2]);
//        }
//    }

    @Test
    public void checkHolidayExists() {
        LocalDateTime localDateTime = LocalDateTime.of(2022, 2, 5, 0, 0);
        System.out.println(holidaysHandler.dateHasHoliday(localDateTime));
    }

//    @Test
//    public void checkInfo(){ holidaysHandler.printArray(); }

}
