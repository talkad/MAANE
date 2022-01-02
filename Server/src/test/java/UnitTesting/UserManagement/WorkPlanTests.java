package UnitTesting.UserManagement;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.UserController;
import Domain.UsersManagment.WorkPlan;
import Domain.WorkPlan.Goal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WorkPlanTests {
    WorkPlan workPlan;
    Goal goal1;
    Goal goal2;

    @Before
    public void setUp(){
        workPlan = new WorkPlan(2021);
        goal1 = new Goal(1, "a", "aa", 100);
        goal2 = new Goal(2, "b", "bb", 200);
        goal2 = new Goal(3, "c", "cc", 300);
    }

    @Test
    public void addOneSchoolsSuccess(){
        Response<Boolean> res = workPlan.insertActivityToFirstAvailableDate(new Pair<>("school1", goal1));
        Assert.assertFalse(res.isFailure());

        String activity = workPlan.getCalendar().get("2021-09-01");
        Assert.assertEquals("Activity a is scheduled for school school1", activity);
    }

    @Test
    public void addTwoSchoolsSuccess(){
        Response<Boolean> res = workPlan.insertActivityToFirstAvailableDate(new Pair<>("school2", goal1), new Pair<>("school3", goal2));
        Assert.assertFalse(res.isFailure());
    }
}
