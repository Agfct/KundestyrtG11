package modules;

import vlc.VLCController;

public class Testingmodules {
	private VLCController vlc;
	public TimelineModule tlmodul;
	
	
	public Testingmodules(){
		this.vlc = new VLCController("C:\\Program Files\\VideoLAN\\VLC64");
		tlmodul = new TimelineModule(vlc);
		TimelineModel tlm = tlmodul.getTimelines().get(0);
		MediaObject mO = new MediaObject("C:\\Users\\EirikZimmer\\Videos\\video_test_512kb.mp4","test",MediaSourceType.VIDEO);
		tlmodul.addDisplay(0);
		tlmodul.assignTimeline(0, tlm);
		tlm.addTimelineMediaObject(1000, 15000, mO);
		//tlm.addTimelineMediaObject(6000,10000,mO);
		//tlm.getTimelineMediaObjects().get(0).setStartPoint(10000);
		tlmodul.playOne(0,0);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tlmodul.pauseOne(0);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tlmodul.playOne(0, 2000);
	}
	public static void main(String[] args) {
		Testingmodules kk = new Testingmodules();
		
		// TODO Auto-generated method stub

	}

}
