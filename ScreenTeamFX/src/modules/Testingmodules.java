package modules;

import java.util.ArrayList;

import vlc.VLCController;

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
		this.vlc = new VLCController(ll);
		this.aka = new WindowDisplay(1);
		tlmodul = new SessionModule(vlc,aka);
		tlmodul.addTimeline();
		TimelineModel tlm = tlmodul.getTimelines().get(1);
		MediaObject mO = new MediaObject("C:\\Users\\EirikZimmer\\Videos\\video_test_512kb.mp4","test",MediaSourceType.VIDEO);
		tlmodul.updateDisplays(ll);
		//tlmodul.addDisplay(1);
		tlmodul.addTimeline();
		TimelineModel tlm2 = tlmodul.getTimelines().get(2);
		tlmodul.assignTimeline(0, tlm);
		System.out.println(tlmodul.getAvailableDisplays());
//		tlmodul.assignTimeline(1, tlm2);
		tlm.addTimelineMediaObject(0,4000,mO);
		tlm.addTimelineMediaObject(6000, 5000,mO);
		tlm.addTimelineMediaObject(11000,1000,mO);
		tlm2.addTimelineMediaObject(0,6000,mO);
		tlm2.addTimelineMediaObject(6000,7000,mO);
		//tlm.addTimelineMediaObject(9000,1500,mO);
		tlm.getTimelineMediaObjects().get(0).setStartPoint(5000);
		tlm.getTimelineMediaObjects().get(1).setStartPoint(10000);
//		tlm2.getTimelineMediaObjects().get(0).setStartPoint(2000);
//		tlmodul.playOne(1,0);
		/*Thread.sleep(2000);
		tlmodul.pauseOne(1);
		Thread.sleep(1000);
		tlmodul.playOne(1,2000);
		Thread.sleep(5000);
		tlmodul.pauseOne(1);
		Thread.sleep(1000);
		tlmodul.playOne(1, 7000);*/
		Thread.sleep(1000);
		aka.WindowManipulation("GitHub", false, 0);
		Thread.sleep(2000);
		aka.WindowManipulation("GitHub", true, 0);
		aka.WindowManipulation("scr 1", false, 0);
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
