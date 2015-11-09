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
		ll.add(4);
		this.vlc = new VLCController(ll);
		this.aka = new WindowDisplay(5);
		tlmodul = new SessionModule(vlc,aka);
		tlmodul.addTimeline();
		TimelineModel tlm = tlmodul.getTimelines().get(1);
		MediaObject mO = new MediaObject("C:\\Users\\caruso\\Videos\\vlc-record-2015-10-20-16h34m55s-dshow___-.avi","test",MediaSourceType.VIDEO);
		tlmodul.updateDisplays(ll);
		//tlmodul.addDisplay(1);
		tlmodul.addTimeline();
		TimelineModel tlm2 = tlmodul.getTimelines().get(2);
		tlmodul.addTimeline();
		TimelineModel tlm3 = tlmodul.getTimelines().get(3);
		tlmodul.assignTimeline(1, tlm);
		tlmodul.assignTimeline(2, tlm2);
		tlmodul.assignTimeline(4, tlm3);
		tlm.addTimelineMediaObject(0,3000,mO);
		tlm.addTimelineMediaObject(3000,mO.getLength(),mO);
		tlm2.addTimelineMediaObject(0,5000,mO);
		tlm2.addTimelineMediaObject(5000,mO.getLength(),mO);
		tlm3.addTimelineMediaObject(0,mO.getLength(),mO);
		tlmodul.playAll();
		String aaa = "canon_norway-DWEU.mkv - VLC";
		String bbb = "20th Century Fox - Fantastic Four: Rise of the Silver Surfer - Theatrical Trailer - VLC";
		Thread.sleep(2000);
		vlc.showmp(tlm.getID(), false);
		aka.WindowManipulation(aaa, false, 1);
		vlc.maximize(tlm.getID());
		Thread.sleep(2000);
		vlc.showmp(tlm.getID(), true);
		aka.WindowManipulation(aaa, true, 1);
		vlc.maximize(tlm.getID());
		vlc.showmp(tlm2.getID(), false);
		aka.WindowManipulation(aaa, false, 2);
		vlc.maximize(tlm2.getID());
		Thread.sleep(2000);
		vlc.showmp(tlm2.getID(), true);
		aka.WindowManipulation(aaa, true, 2);
		vlc.maximize(tlm2.getID());
		Thread.sleep(2000);
		vlc.showmp(tlm.getID(), false);
		aka.WindowManipulation("Downloads", false, 1);
		vlc.maximize(tlm.getID());
		vlc.showmp(tlm2.getID(), false);
		aka.WindowManipulation(bbb, false, 2);
		vlc.maximize(tlm2.getID());
		vlc.showmp(tlm3.getID(), false);
		aka.WindowManipulation(aaa, false, 4);
		vlc.maximize(tlm3.getID());
		Thread.sleep(1000);
		vlc.showmp(tlm.getID(), true);
		aka.WindowManipulation("Downloads", true, 1);
		vlc.maximize(tlm.getID());
		vlc.showmp(tlm2.getID(), true);
		aka.WindowManipulation("Windows Task Manager", true, 2);
		vlc.maximize(tlm2.getID());
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
