package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import modules.MediaObject;
import modules.MediaSourceType;

public class MediaObjectTest extends TestCase  {
	private MediaObject MO;
	@Before
	public void setUp() throws Exception {
		super.setUp();
		MO = new MediaObject("Path","Name",modules.MediaSourceType.IMAGE);
		
		//TODO: Length is random is we don't choose Image or Window
		// WE need to fix it !!
		// We shouldn't forget to fix it
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testgetName() {
		assertEquals("Correc getName","Name",MO.getName());
	}
	public void testsetName() {
		MO.setName("NewName");
		assertEquals("Correc setName","NewName",MO.getName());
	}
	public void testgetPath() {
		assertEquals("Correc getPath","Path",MO.getPath());
	}
	public void testsetPath() {
		MO.setPath("NewPath");
		assertEquals("Correc setPath","NewPath",MO.getPath());
	}
	public void testgetLength() {
		assertEquals("Correc getLength",Long.MAX_VALUE,MO.getLength());
		MO.setLength(10);
		assertEquals("Correc getLength",10,MO.getLength());
		MO.setLength(-10);
		assertEquals("Correc getLength",-10,MO.getLength());
		
		//Should have a fix size from the begginning
		MediaObject MO2 = new MediaObject("Path","Name",modules.MediaSourceType.VIDEO);
		//assertEquals("Correc getLength",0,MO2.getLength());
	}
	public void testsetLength() {
		MO.setLength(40);
		assertEquals("Correc setLength ",40,MO.getLength());
		
		MO.setLength(-40);
		assertEquals("Correc setLength ",-40,MO.getLength());
		
		MO.setLength((long) 1000000000);
		assertEquals("Correc setLength ",1000000000,MO.getLength());
	}
	public void testgetType() {
		assertEquals("Correc getType",modules.MediaSourceType.IMAGE,MO.getType());
	}
	public void testsetType() {
		MO.setType(modules.MediaSourceType.AUDIO);
		assertEquals("Correc setType",modules.MediaSourceType.AUDIO,MO.getType());
		MO.setType(modules.MediaSourceType.VIDEO);
		assertEquals("Correc setType",modules.MediaSourceType.VIDEO,MO.getType());
		MO.setType(modules.MediaSourceType.WINDOW);
		assertEquals("Correc setType",modules.MediaSourceType.WINDOW,MO.getType());
		MO.setType(modules.MediaSourceType.STREAM);
		assertEquals("Correc setType",modules.MediaSourceType.STREAM,MO.getType());
	}
	

}
