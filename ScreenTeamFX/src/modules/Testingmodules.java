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
		this.vlc = new VLCController(ll);
		this.aka = new WindowDisplay(2);
		tlmodul = new SessionModule(vlc,aka);
		tlmodul.addTimeline();
		TimelineModel tlm = tlmodul.getTimelines().get(1);
		MediaObject mO = new MediaObject("C:\\Users\\EirikZimmer\\Videos\\video_test_512kb.mp4","test",MediaSourceType.VIDEO);
		tlmodul.updateDisplays(ll);
		//tlmodul.addDisplay(1);
		tlmodul.addTimeline();
		TimelineModel tlm2 = tlmodul.getTimelines().get(2);
		VLCMediaPlayer mp = vlc.getMediaPlayerList().get(tlm2.getID());
		VLCMediaPlayer mp1 = vlc.getMediaPlayerList().get(tlm.getID());
		tlmodul.assignTimeline(0, tlm);
		tlmodul.assignTimeline(1, tlm2);
		tlm.addTimelineMediaObject(0,14000,mO);
		tlm.addTimelineMediaObject(15000, 5000,mO);
		tlm.addTimelineMediaObject(20000,1000,mO);
		tlm2.addTimelineMediaObject(0,20000,mO);
		tlm2.addTimelineMediaObject(20000,7000,mO);
		//tlm.addTimelineMediaObject(9000,1500,mO);
		tlm.getTimelineMediaObjects().get(0).setStartPoint(5000);
		tlm.getTimelineMediaObjects().get(1).setStartPoint(10000);
//		tlm2.getTimelineMediaObjects().get(0).setStartPoint(2000);
		tlmodul.playAll();
		Thread.sleep(2000);
		mp.showhide();
		aka.WindowManipulation("GitHub", false, 1);
		mp.maximize();
		mp.maximize();
		Thread.sleep(2000);
		mp.showhide();
		aka.WindowManipulation("GitHub", true, 1);
		mp.maximize();
		mp.maximize();
		Thread.sleep(2000);
		mp1.showhide();
		aka.WindowManipulation("GitHub", false, 0);
		mp1.maximize();
		Thread.sleep(2000);
		mp1.showhide();
		aka.WindowManipulation("GitHub", true, 0);
		Thread.sleep(2000);
		//aka.WindowManipulation("GitHub", true, 0);
		mp.showhide();
		aka.WindowManipulation("GitHub", false, 1);
		mp.maximize();
		Thread.sleep(2000);
		aka.WindowManipulation("GitHub", true, 1);
		mp.showhide();
		Thread.sleep(2000);
		mp1.showhide();
		aka.WindowManipulation("GitHub", false, 0);
		mp1.maximize();
		Thread.sleep(2000);
		aka.WindowManipulation("GitHub", true, 0);
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
