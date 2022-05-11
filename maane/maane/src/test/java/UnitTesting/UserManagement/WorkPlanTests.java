package UnitTesting.UserManagement;

import Domain.CommonClasses.Pair;
import Domain.CommonClasses.Response;
import Domain.UsersManagment.Activity;
import Domain.WorkPlan.Goal;
import Domain.WorkPlan.WorkPlan;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class WorkPlanTests {
    WorkPlan workPlan;
    Goal goal1;
    Goal goal2;

    @Before
    public void setUp(){
        workPlan = new WorkPlan(2021);
        goal1 = new Goal(1, "a", "aa", 1, 1, "tech", "2022");
        goal2 = new Goal(2, "b", "bb", 2, 2, "tech", "2022");
        goal2 = new Goal(3, "c", "cc", 3, 3, "tech", "2022");
    }

    @Test
    public void addOneSchoolsSuccess(){
        Response<Boolean> res = workPlan.insertActivityToFirstAvailableDate(new Pair<>("school1", goal1));
        Assert.assertFalse(res.isFailure());

        List<Activity> activity = workPlan.getCalendar().get("2021-09-01");
        Assert.assertEquals("school1", activity.get(0).getSchool());
    }

    @Test
    public void addTwoSchoolsSuccess(){
        Response<Boolean> res = workPlan.insertActivityToFirstAvailableDate(new Pair<>("school2", goal1), new Pair<>("school3", goal2));
        Assert.assertFalse(res.isFailure());
    }

    @Test
    public void getFromMonthSuccess(){
        Map<String, List<Activity>> result = workPlan.getScheduleFromMonth("4");
        Assert.assertTrue(result.size()==51);
    }
}
