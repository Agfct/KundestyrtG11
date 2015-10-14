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
		tlm.addTimelineMediaObject(1000,10000,mO);
		tlm.addTimelineMediaObject(10010,1000,mO);
		//tlm.addTimelineMediaObject(9000,1500,mO);
		//tlm.getTimelineMediaObjects().get(0).setStartPoint(10000);
		tlmodul.playOne(0,5000);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			tlmodul.pauseOne(0);
		} catch (InterruptedException e1) {
			System.out.println("good");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tlmodul.playOne(0, 7000);
	}
	public static void main(String[] args) {
		Testingmodules kk = new Testingmodules();
		
		// TODO Auto-generated method stub

	}

}
