package modules;

import java.io.File;
import java.util.ArrayList;

public class StorageControllerTestClass {
	public static void main(String[] args) {
		
		File savefile = new File("storagecontroller_test.data");
		StorageController sc = StorageController.getInstance();
		ArrayList<MediaObject> mediaobjects = new ArrayList<MediaObject>();
		SessionModule smModule = new SessionModule(); 
		
		//MediaObject mo = new MediaObject(path, name, type)
		MediaObject mo1 = new MediaObject("some/path", "Test parent 1", MediaSourceType.VIDEO);
		MediaObject mo2 = new MediaObject("some/other/path", "Test parent 2", MediaSourceType.VIDEO);
		MediaObject mo3 = new MediaObject("User/Videos/Not_Porn(HD)", "Test parent 3", MediaSourceType.VIDEO);
		// Put the mediaobjects in an arraylist
		mediaobjects.add(mo1);
		mediaobjects.add(mo2);
		mediaobjects.add(mo3);
		
		//TimelineModel tlm = new TimelineModel(new ArrayList<TimelineMediaObject>(), id);
		TimelineModel tlm1 = new TimelineModel(1);
		TimelineModel tlm2 = new TimelineModel(2);
		TimelineModel tlm3 = new TimelineModel(3);
		ArrayList<TimelineModel> timelineList = new ArrayList<TimelineModel>();
		timelineList.add(tlm1);
		timelineList.add(tlm2);
		timelineList.add(tlm3);
		smModule.setTimelines(timelineList);
		
		//TimelineMediaObject tlmo = new TimelineMediaObject(start, duration, timelineid, parent)
		TimelineMediaObject tlmo1  = new TimelineMediaObject(1, 11, 1, mo1);
		TimelineMediaObject tlmo2  = new TimelineMediaObject(2, 12, 1, mo2);
		TimelineMediaObject tlmo3  = new TimelineMediaObject(3, 13, 1, mo3);
		tlm1.addTimelineMediaObject(tlmo1);
		tlm1.addTimelineMediaObject(tlmo2);
		tlm1.addTimelineMediaObject(tlmo3);
		
		TimelineMediaObject tlmo4  = new TimelineMediaObject(4, 14, 2, mo1);
		TimelineMediaObject tlmo5  = new TimelineMediaObject(5, 15, 2, mo2);
		tlm2.addTimelineMediaObject(tlmo4);
		tlm2.addTimelineMediaObject(tlmo5);
		
		TimelineMediaObject tlmo6  = new TimelineMediaObject(6, 16, 3, mo3);
		TimelineMediaObject tlmo7  = new TimelineMediaObject(7, 17, 3, mo3);
		TimelineMediaObject tlmo8  = new TimelineMediaObject(8, 18, 3, mo3);
		TimelineMediaObject tlmo9  = new TimelineMediaObject(9, 19, 3, mo3);
		TimelineMediaObject tlmo10 = new TimelineMediaObject(10, 110, 3, mo3);
		tlm3.addTimelineMediaObject(tlmo6);
		tlm3.addTimelineMediaObject(tlmo7);
		tlm3.addTimelineMediaObject(tlmo8);
		tlm3.addTimelineMediaObject(tlmo9);
		tlm3.addTimelineMediaObject(tlmo10);
		
		System.out.println("Result success = " + sc.storeSession(smModule, savefile));
		
		SessionModule loadedSM = sc.loadSession(savefile);
		
		/**
		 *  Put a break point here and use the debugger to check that the saved and loaded objects are the same.
		 *  (Or write code to do it for you)
		 */
		System.out.println("debug_point");
		
		System.out.println("Finnished");
	}
}