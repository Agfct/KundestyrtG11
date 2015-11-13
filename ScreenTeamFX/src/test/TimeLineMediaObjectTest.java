/**
 * 
 */
package test;

import static org.junit.Assert.*;
import java.io.Serializable;

import org.junit.runner.RunWith;

import junit.framework.TestCase;
import modules.MediaObject;
import modules.MediaSourceType;
import modules.TimelineMediaObject;

/**
 * @author Baptiste
 *
 */

/**
 * An abstract test case with spring runner configuration, used by all test cases.
 */


public class  TimeLineMediaObjectTest  extends TestCase {

	/**
	 * @throws java.lang.Exception
	 */	
	private TimelineMediaObject TLMO;
	private MediaObject MO;
	
	/**
	 * @return 
	 * @throws java.lang.Exception
	 */
	
	public void setUp() throws Exception {
		super.setUp();
		//start =10
		//spoint=0
		// duration=20
		// timelineid=1
		//parent=null
		MO= new MediaObject("Path","name",modules.MediaSourceType.VIDEO);
		MO.setLength(10);
		TLMO= new TimelineMediaObject(10,0,20,1,MO);
	}

	public void testgetStart() {
		assertEquals("Correc start",10,TLMO.getStart());
	}
	public void testsetStart() {		
		TLMO.setStart(35);
		assertEquals("Correc start",35,TLMO.getStart());
	}
	public void testgetDuration() {
		assertEquals("Correc Duration",20,TLMO.getDuration());
	}
	//Problem due to the "min" between maxduration and the newduration
	public void testsetDuration() {
		TLMO.setDuration(10);
		//Math.min(TLMO.getParent().getLength()-TLMO.getStartPoint(),10)
		assertEquals("Correc Duration",10,TLMO.getDuration());

	}
	public void testgetTimelineid() {
		assertEquals("Correc timelineid",1,TLMO.getTimelineid());
	}
	public void testsetTimelineid() {
		TLMO.setTimelineid(2);
		assertEquals("Correc timelineid",2,TLMO.getTimelineid());
	}
	public void testgetEnd() {
		assertEquals("Correc end",30,TLMO.getEnd());
	}
	public void testgetParent() {
		assertEquals("Correc Parent",MO,TLMO.getParent());
	}
	public void testsetParent() {
		MediaObject blabla= new MediaObject("path","blabla",modules.MediaSourceType.WINDOW );
		TLMO.setParent(blabla);
		assertEquals("Correc Parent WINDOW",blabla,TLMO.getParent());
		blabla.setType(modules.MediaSourceType.AUDIO);
		TLMO.setParent(blabla);
		assertEquals("Correc Parent AUDIO",blabla,TLMO.getParent());
		blabla.setType(modules.MediaSourceType.STREAM);
		TLMO.setParent(blabla);
		assertEquals("Correc Parent STREAM",blabla,TLMO.getParent());
		blabla.setType(modules.MediaSourceType.VIDEO);
		TLMO.setParent(blabla);
		assertEquals("Correc Parent VIDEO",blabla,TLMO.getParent());
		blabla.setType(modules.MediaSourceType.IMAGE);
		TLMO.setParent(blabla);
		assertEquals("Correc Parent IMAGE",blabla,TLMO.getParent());

	}
	public void testgetStartPoint() {
		assertEquals("Correc startpoint",0,TLMO.getStartPoint());
	}
	public void testsetStartPoint() {
	//	TLMO.setStartPoint(-10);
	//	assertEquals("Correc startpoint",-10,TLMO.getStartPoint());
		TLMO.setStartPoint(20);
		assertEquals("Correc startpoint",20,TLMO.getStartPoint());
	}
	

}
