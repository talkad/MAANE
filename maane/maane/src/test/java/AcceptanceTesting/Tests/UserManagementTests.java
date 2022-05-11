package AcceptanceTesting.Tests;

import AcceptanceTesting.Bridge.ProxyBridgeUser;
import org.junit.Before;
import org.junit.Test;

public class UserManagementTests {


    private ProxyBridgeUser proxyUser;

    @Before
    public void setUp(){

    }

    /**
     * admin create a supervisor in 'tech' workfield
     * the supervisor assign instructor and coordinator to the same rules
     * the instructor update its info
     * the supervisor transfer supervision to another instructor
     * the supervisor remove the new supervisor from the system
     */
    @Test
    public void legalSystemUseTest(){

    }


    /**
     * admin create a supervisor in 'tech' workfield
     * create new goals and survey answers
     * generate a new workplan accordingly
     */
    @Test
    public void workPlanGeneratingTests(){
        // ...
    }



}
