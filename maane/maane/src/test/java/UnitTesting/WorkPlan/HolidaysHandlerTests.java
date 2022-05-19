package UnitTesting.WorkPlan;
import Communication.Initializer.ServerContextInitializer;
import Domain.WorkPlan.HolidaysHandler;
import org.junit.Before;
import org.junit.Test;

public class HolidaysHandlerTests {

    private HolidaysHandler holidaysHandler;

    @Before
    public void setup(){
        ServerContextInitializer.getInstance().setMockMode();
        holidaysHandler = new HolidaysHandler(2022);
    }

    @Test
    public void checkInsert() {

    }

    @Test
    public void checkInfo(){
        holidaysHandler.printArray();
    }

}
