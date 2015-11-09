package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ MediaObjectTest.class, SessionModuleTest.class, TimeLineMediaObjectTest.class,
		TimelineModelTest.class })
public class AllTests {

}
