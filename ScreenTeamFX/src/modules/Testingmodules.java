package modules;

import java.util.ArrayList;

import vlc.VLCController;
import vlc.VLCMediaPlayer;

public class Testingmodules {
	private VLCController vlc;
	public SessionModule tlmodul;
	private WindowDisplay aka;
	
	/**
	 * Just a class to test the integration between the module and VLC
	 * @throws InterruptedException 
	 */
	public Testingmodules() throws InterruptedException{
		ArrayList<Integer> ll = new ArrayList<Integer>();
		ll.add(0);
		ll.add(1);
		ll.add(2);
		ll.add(3);
		this.vlc = new VLCController(ll);
		this.aka = new WindowDisplay(4);
		tlmodul = new SessionModule(vlc,aka);
		tlmodul.addTimeline();
		TimelineModel tlm = tlmodul.getTimelines().get(1);
		MediaObject mO = new MediaObject("C:\\Users\\caruso\\Videos\\vlc-record-2015-10-20-16h34m55s-dshow___-.avi","test",MediaSourceType.VIDEO);
		tlmodul.updateDisplays(ll);
		//tlmodul.addDisplay(1);
		tlmodul.addTimeline();
		TimelineModel tlm2 = tlmodul.getTimelines().get(2);
		VLCMediaPlayer mp = vlc.getMediaPlayerList().get(tlm2.getID());
		VLCMediaPlayer mp1 = vlc.getMediaPlayerList().get(tlm.getID());
		tlmodul.assignTimeline(2, tlm);
		tlmodul.assignTimeline(1, tlm2);
		tlm.addTimelineMediaObject(0,19000,mO);
//		tlm.addTimelineMediaObject(15000, 5000,mO);
//		tlm.addTimelineMediaObject(20000,1000,mO);
		tlm2.addTimelineMediaObject(0,19000,mO);
//		tlm2.addTimelineMediaObject(20000,7000,mO);
		//tlm.addTimelineMediaObject(9000,1500,mO);
//		tlm.getTimelineMediaObjects().get(0).setStartPoint(5000);
//		tlm.getTimelineMediaObjects().get(1).setStartPoint(10000);
//		tlm2.getTimelineMediaObjects().get(0).setStartPoint(2000);
		tlmodul.playAll();
		Thread.sleep(2000);
		mp.showhide();
		aka.WindowManipulation("Windows Task Manager", false, 1);
		mp.maximize();
		Thread.sleep(2000);
		mp.showhide();
		mp1.showhide();
		aka.WindowManipulation("Windows Task Manager", true, 1);
		aka.WindowManipulation("Windows Task Manager", false, 2);
		mp.maximize();
		Thread.sleep(2000);
		mp1.showhide();
		aka.WindowManipulation("Windows Task Manager", true, 2);
		Thread.sleep(2000);
		//aka.WindowManipulation("GitHub", true, 0);
		mp.showhide();
		aka.WindowManipulation("Downloads", false, 1);
		mp.maximize();
		mp1.showhide();
		aka.WindowManipulation("Windows Task Manager", false, 2);
		mp1.maximize();
		Thread.sleep(1000);
		aka.WindowManipulation("Downloads", true, 1);
		mp.showhide();
		mp1.showhide();
		aka.WindowManipulation("Windows Task Manager", true, 2);
		mp1.maximize();
		Thread.sleep(2000);
		//aka.WindowManipulation("GitHub", true, 0);
	}
	public static void main(String[] args) {
		try {
			Testingmodules kk = new Testingmodules();
		} catch (InterruptedException e) {
			System.out.println("interrupted sleep");
		}
		
		// TODO Auto-generated method stub

	}

}
