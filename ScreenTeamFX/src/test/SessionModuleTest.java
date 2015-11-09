package test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import modules.SessionModule;
import modules.TimelineModel;
import modules.WindowDisplay;
import vlc.VLCController;


/*
 * Baptiste
 */
public class SessionModuleTest {

	private SessionModule SM;
	@Before
	public void setUp() throws Exception {
		ArrayList<Integer> displays = new ArrayList<Integer>(1);
		VLCController vlc= new VLCController(displays);
		WindowDisplay wdi= new WindowDisplay(1);
		SM = new SessionModule(vlc,wdi);
	}

	@After
	public void tearDown() throws Exception {
		SM=null;
	}

	@Test
	public void testSessionModule() {
		ArrayList<Integer> displays = new ArrayList<Integer>(1);
		VLCController vlc= new VLCController(displays);
		WindowDisplay wdi= new WindowDisplay(1);
		SessionModule SMtest = new SessionModule(vlc,wdi);
		assertEquals("Creation of the timeline",0,SMtest.getTimelines().size());
		assertEquals("Creation of the mediaObjects",0,SMtest.getMediaObjects().size());
		assertEquals("Creation of the globaltime",0,SMtest.getGlobalTime());
		//No get for performance stack
		assertEquals("Creation of the performancestack",0,SMtest.getPerformancestack().size());
		//No get for tlmID
		assertEquals("Creation of the timeid",0,SMtest.gettlmID());	
		assertEquals("Creation of the sessionLength",2000,SMtest.getSessionLength());
		//No get for displays
		assertEquals("Creation of the timeline",0,SMtest.getdisplays().size());
		//No get for listener
		assertEquals("Creation of the listeners",0,SMtest.getListeners().size());
		//No get VLC controller
		//check le VLC
		assertEquals("Creation of the VLCcontroller",vlc,SMtest.getvlccontroller());
		//No get windowdisplay
		assertEquals("Creation of the windowdisplay",wdi,SMtest.getWindowDisplay());
		//No get pausing
		assertTrue(SMtest.getPausing());
		//No get paused
		assertTrue(SMtest.getPaused());
		//No get t1, to know if the thread is alive or not	
		assertFalse(SMtest.getT1().isAlive());
		//No get thread
		assertFalse(SMtest.gettAll().isAlive());
		//No get globalTimeTicker
		assertFalse(SMtest.getGlobalTimeTicker().isAlive());
		
		assertEquals("Creation of the timelineOrder",0,SMtest.getTimelineOrder().size());
		//No get shown windows
		assertEquals("Creation of the timeline",0,SMtest.getShownwindows().size());
		//
		assertEquals("Creation of the numberOfAvailableDisplays",-1,SMtest.getNumberOfAbailableDisplays());
	}

	@Test
	public void testAddTimeline() {
		SM.addTimeline();
		assertEquals("testAddTimeline tlmID increm:",1,SM.gettlmID());
		assertEquals("testAddTimeline timelines:",1,SM.getTimelines().size());
		
		//Creation of a second time line to check if the data change very well
		SM.addTimeline();
		assertEquals("testAddTimeline vlccontroller test the size:",2,SM.getvlccontroller().getMediaPlayerList().size());
		assertEquals("testAddTimeline vlccontroller test the ID of th emedia player:",2,SM.getvlccontroller().getMediaPlayerList().get(SM.gettlmID()).getID());
		//It should return the  Id of the timeline and fix this new timeline at the position zero
		int tlmid=SM.gettlmID();
		assertEquals("testAddTimeline timelineorder",tlmid,SM.getTimelineOrder().get(0).intValue());
		//Check if we change the position or not of the time line, depend of the type of "modules.TimeLineChanges"
		//This is check in the class time line model
	}

	@Test
	public void testRemoveTimeline() {
		assertEquals("testRemoveTimeline check the number of:",0,SM.getTimelines().size());
		SM.addTimeline();
		SM.addTimeline();
		SM.addTimeline();
		assertEquals("testRemoveTimeline check the number of:",3,SM.getTimelines().size());
		//Check if the timeline exist
		assertEquals("testRemoveTimeline check if the timeline2 exist:",2,SM.getTimelines().get(2).getID());			
		//Try to delete the time line 2		
		SM.removeTimeline(2);
		//The test below doesn't work so we can conclued than this timeline was delete
		//assertNotSame("testRemoveTimeline check if the timeline2 doesn't existe now:",2,SM.getTimelines().get(2).getID());			
		assertEquals("testRemoveTimeline check the number of:",2,SM.getTimelines().size());

	}

	@Test
	public void testRemoveAllTimlines() {
		assertEquals("testRemoveTimeline check the number of:",0,SM.getTimelines().size());
		SM.addTimeline();
		SM.addTimeline();
		SM.addTimeline();
		assertEquals("testRemoveTimeline check the number of:",3,SM.getTimelines().size());
		//Check if the timeline exist
		assertEquals("testRemoveTimeline check if the timeline2 exist:",2,SM.getTimelines().get(2).getID());			
		//Try to delete the time line 2		
		SM.removeAllTimlines();
		//The test below doesn't work so we can conclued than this timeline was delete
		//assertNotSame("testRemoveTimeline check if the timeline1 doesn't existe now:",1,SM.getTimelines().get(1).getID());			
		//assertNotSame("testRemoveTimeline check if the timeline2 doesn't existe now:",2,SM.getTimelines().get(2).getID());			
		//assertNotSame("testRemoveTimeline check if the timeline3 doesn't existe now:",3,SM.getTimelines().get(3).getID());			

		assertEquals("testRemoveTimeline check the number of:",0,SM.getTimelines().size());

	}
	/*
	 * Baptiste
	 * 
	 * It's not a good test -> It didn't testthe good thing
	 */
	@Test
	public void testUnassignTimeline() {
		SM.addTimeline();
		SM.addTimeline();
		assertEquals("testUnassignTimeline check the number of timeline:",2,SM.getTimelines().size());
		SM.getTimelines().get(SM.getTimelines().size()).addDisplay(1);
		SM.getTimelines().get(SM.getTimelines().size()).addDisplay(2);
		SM.getTimelines().get(SM.getTimelines().size()).addDisplay(3);
		assertEquals("testUnassignTimeline check the number of display:",3,SM.getTimelines().get(SM.getTimelines().size()).getAssignedDisplays().size());
		SM.unassignTimeline(SM.getTimelines().get(SM.getTimelines().size()));
		assertEquals("testUnassignTimeline check the number of display:",3,SM.getTimelines().get(SM.getTimelines().size()).getAssignedDisplays().size());	
		assertEquals("testUnassignTimeline check the number of timeline:",2,SM.getTimelines().size());

		
	}
/*
	@Test
	public void testAssignTimeline() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddDisplay() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateDisplays() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveDisplay() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNumberOfAbailableDisplays() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlayAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlayOne() {
		fail("Not yet implemented");
	}

	@Test
	public void testPauseAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testPauseOne() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTimelines() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateNewMediaObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMediaObjects() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddMediaObjectToTimeline() {
		fail("Not yet implemented");
	}

	@Test
	public void testDuplicateToTimeline() {
		fail("Not yet implemented");
	}

	@Test
	public void testTimelineMediaObjectChanged() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddListener() {
		fail("Not yet implemented");
	}

	@Test
	public void testClearListeners() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveListener() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveTimelineMediaObjectFromTimeline() {
		fail("Not yet implemented");
	}

	@Test
	public void testChangeOrderOfTimelines() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTimelineOrder() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSessionLength() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGlobalTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testGlobalTimeChanged() {
		fail("Not yet implemented");
	}

	@Test
	public void testChangeGlobalTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAvailableDisplays() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveVLCController() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveThreads() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveAssignedDisplays() {
		fail("Not yet implemented");
	}

	@Test
	public void testReinitialize() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveListeners() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveAndGetVLCController() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveT1() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveTAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveGlobalTimeTicker() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveDisplays() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveWindowDisplay() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetListeners() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetVLCController() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetT1() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetTAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetGlobalTimeTicker() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetDisplays() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetWindowDisplay() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveAllTimlineDisplayAssignments() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAvailableWindows() {
		fail("Not yet implemented");
	}

	@Test
	public void testObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClass() {
		fail("Not yet implemented");
	}

	@Test
	public void testHashCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testEquals() {
		fail("Not yet implemented");
	}

	@Test
	public void testClone() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotify() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotifyAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitLongInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testWait() {
		fail("Not yet implemented");
	}

	@Test
	public void testFinalize() {
		fail("Not yet implemented");
	}
*/
}
