package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import modules.MediaObject;
import modules.MediaSourceType;
import modules.TimelineMediaObject;
import modules.TimelineModel;


public class TimelineModelTest extends TestCase {

	private TimelineModel TLM;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		TLM = new TimelineModel(1);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testgetID() {
		assertEquals("Correc id",1,TLM.getID());
	}
	public void testgetTimelineStack() {
		//System.out.println("Check TimelineStack format");
		assertEquals("Timeline just create no value on it",0,TLM.getTimelineStack().size());
	}
	
	// We need to brush up this test, we don't test enough thing
	public void testaddTimelineMediaObject() {
		
		assertEquals("TimelineMediaObjects should be just create",0, TLM.getTimelineMediaObjects().size());
		MediaObject MO= new MediaObject("path","name",MediaSourceType.VIDEO);
		MO.setLength(10);
		TimelineMediaObject TMOadd= new TimelineMediaObject(0,0,10,1,MO);
		TLM.addTimelineMediaObject(TMOadd);
		assertEquals("add one element addtTimelineMediaObjects Test Size",1,TLM.getTimelineMediaObjects().size());
		
		//Test different addtimelineMediaObject
		String output;
		//TimeLine have no element	
		MO.setPath("path");
		MO.setName("Test1");
		MO.setLength(100);
		MO.setType(MediaSourceType.VIDEO);
		TMOadd.setStart(0);
		TMOadd.setStartPoint(4);
		TMOadd.setTimelineid(1);
		TMOadd.setDuration(5);
		TMOadd.setParent(MO);
		output = TLM.addTimelineMediaObject(TMOadd);
		//System.out.println("addtimelineMediaObject Test1: "+ output);
		//Test every assignement
		assertEquals("testaddTimelineMediaObject Test Start",0,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getStart());
		assertEquals("testaddTimelineMediaObject Test StartPoint",4,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getStartPoint());
		assertEquals("testaddTimelineMediaObject Test TimelineId",1,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getTimelineid());
		assertEquals("testaddTimelineMediaObject Test Duration",5,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getDuration());	
		assertEquals("testaddTimelineMediaObject Test End",5,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getEnd());
		assertEquals("testaddTimelineMediaObject Test Parent, path","path",TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getParent().getPath());
		assertEquals("testaddTimelineMediaObject Test Parent, Name","Test1",TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getParent().getName());
		assertEquals("testaddTimelineMediaObject Test Parent, Type",MediaSourceType.VIDEO,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getParent().getType());
		
		
		
		//Initialization of the timelinemediaobject	
		MO.setPath("path2");
		MO.setName("Test2");
		MO.setLength(10);
		MO.setType(MediaSourceType.AUDIO);
		TMOadd.setStart(15);
		TMOadd.setStartPoint(4);
		TMOadd.setTimelineid(2);
		TMOadd.setDuration(10);
		TMOadd.setParent(MO);
		output = TLM.addTimelineMediaObject(TMOadd);
		
		//System.out.println("addtimelineMediaObject Test2: "+ output);
		//Test every assignement
		assertEquals("testaddTimelineMediaObject Test Start",15,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getStart());
		assertEquals("testaddTimelineMediaObject Test StartPoint",4,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getStartPoint());
		assertEquals("testaddTimelineMediaObject Test TimelineId",2,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getTimelineid());
		assertEquals("testaddTimelineMediaObject Test Duration: min(maxDur=10-4,newDur=10)",6,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getDuration());	
		assertEquals("testaddTimelineMediaObject Test End 15+(real duration)6",21,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getEnd());
		assertEquals("testaddTimelineMediaObject Test Parent, path","path2",TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getParent().getPath());
		assertEquals("testaddTimelineMediaObject Test Parent, Name","Test2",TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getParent().getName());
		assertEquals("testaddTimelineMediaObject Test Parent, Type",MediaSourceType.AUDIO,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getParent().getType());
		
		
		// --------------------------------------------------------------------------------------------------------
		// -------------------This test prove than you fix wrong value to a TimeLineMediaObject-------------------
		// --------------------------------------------------------------------------------------------------------
		//Initialization of the timelinemediaobject	
		MO.setPath(null);
		MO.setName(null);
		MO.setLength(0);
		MO.setType(MediaSourceType.WINDOW);
		TMOadd.setStart(15);
		TMOadd.setStartPoint(20);
		TMOadd.setTimelineid(124);
		TMOadd.setDuration(1000000);
		TMOadd.setParent(MO);
		output = TLM.addTimelineMediaObject(TMOadd);		
		//System.out.println("addtimelineMediaObject Test2: "+ output);
		//Test every assignement
		assertEquals("testaddTimelineMediaObject Test Start",15,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getStart());
		// WARNING it keep the old start point
		assertEquals("testaddTimelineMediaObject Test StartPoint",4,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getStartPoint());
		assertEquals("testaddTimelineMediaObject Test TimelineId",124,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getTimelineid());
		assertEquals("testaddTimelineMediaObject Test Duration: min(maxDur=0-4,newDur=1000000)",-4,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getDuration());	
		assertEquals("testaddTimelineMediaObject Test End 15+(real duration=-4)",11,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getEnd());
		assertEquals("testaddTimelineMediaObject Test Parent, path",null,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getParent().getPath());
		assertEquals("testaddTimelineMediaObject Test Parent, Name",null,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getParent().getName());
		assertEquals("testaddTimelineMediaObject Test Parent, Type",MediaSourceType.WINDOW,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMOadd)).getParent().getType());
		
		//System.out.println(TLM.getTimelineMediaObjects().size());
		
	}
	
	public void testremoveTimelineMediaObject() {
		// add a timeline before delete it
		MediaObject MO= new MediaObject("path","name",MediaSourceType.VIDEO);
		MO.setLength(10);
		TimelineMediaObject TMOadd= new TimelineMediaObject(0,0,10,1,MO);
		TLM.addTimelineMediaObject(TMOadd);
		//System.out.println(" Before remove: "+TLM.getTimelineMediaObjects().size());
		TLM.removeTimelineMediaObject(TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().size()-1));
		//System.out.println(" After remove: "+TLM.getTimelineMediaObjects().size());
		assertEquals("Remove one element addtTimelineMediaObjects Test Size",0,TLM.getTimelineMediaObjects().size());
		
	}
	public void testgetTimelineMediaObjects() {
		//System.out.println(" testgetTimelineMediaObjects: "+TLM.getTimelineMediaObjects().size());
		assertEquals("getTimelineMediaObjects Test Size",0,TLM.getTimelineMediaObjects().size());
	}
	public void testtimelineMediaObjectChanged() {
		String output;
		//Initialisation of the Timeline to be sure of our modification
		MediaObject MO= new MediaObject("path","name",modules.MediaSourceType.VIDEO);
		TimelineMediaObject TMO= new TimelineMediaObject(0,0,0,0,MO);
		TLM.addTimelineMediaObject(TMO);
		
		//Modification need to be done		
		//output = TLM.timelineMediaObjectChanged(TMO, 5, 10, 15);
		//System.out.println("testtimelineMediaObjectChanged : "+ output);
		//Check every changement	
		//assertEquals("testtimelineMediaObjectChanged Test Duration",15,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMO)).getDuration());
		//assertEquals("testtimelineMediaObjectChanged Test newInternalStart",10,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMO)).getStartPoint());
		//assertEquals("testtimelineMediaObjectChanged Test NewStart",5,TLM.getTimelineMediaObjects().get(TLM.getTimelineMediaObjects().indexOf(TMO)).getStart());
	}
	public void testtimelinechanged() {
		
	}
	public void testgetAssignedDisplays() {	
		assertEquals("getAssignedDisplays Test Size",0,TLM.getAssignedDisplays().size());
		
	}
	public void testaddDisplay() {
		//System.out.println(" Before add display: "+TLM.getAssignedDisplays().size());
		TLM.addDisplay(3);
		//System.out.println(" After add display: "+TLM.getAssignedDisplays().size());
		//System.out.println(" display index: "+TLM.getAssignedDisplays().indexOf(3));
		assertEquals("addDisplay Test Size",1,TLM.getAssignedDisplays().size());
		assertNotSame("addDisplay Test Same display",-1,TLM.getAssignedDisplays().indexOf(3));

	}
	public void testremoveDisplay() {
		//System.out.println(" Before add display: "+TLM.getAssignedDisplays().size());
		TLM.addDisplay(5);
		
		//System.out.println(" After add display: "+TLM.getAssignedDisplays().size());
		
		TLM.removeDisplay(5);
		//System.out.println(" After remove display: "+TLM.getAssignedDisplays().size());

		assertEquals("testremoveDisplay Test Size",0,TLM.getAssignedDisplays().size());
		
	}
	public void testremoveAllDisplays() {
		//System.out.println(" Before add display: "+TLM.getAssignedDisplays().size());
		TLM.addDisplay(1);
		TLM.addDisplay(2);
		TLM.addDisplay(3);
		TLM.addDisplay(4);
		//System.out.println(" After add display: "+TLM.getAssignedDisplays().size());
		TLM.removeAllDisplays();
		//System.out.println(" After remove All display: "+TLM.getAssignedDisplays().size());

		assertEquals("testremoveAllDisplay Test Size",0,TLM.getAssignedDisplays().size());
		
	}
	


}
