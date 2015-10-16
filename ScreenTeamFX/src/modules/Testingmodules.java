package modules;

import vlc.VLCController;

public class Testingmodules {
	private VLCController vlc;
	public SessionModule tlmodul;
	
	/**
	 * Just a class to test the integration between the module and VLC
	 * @throws InterruptedException 
	 */
	public Testingmodules() throws InterruptedException{
		this.vlc = new VLCController("C:\\Program Files\\VideoLAN\\VLC64");
		tlmodul = new SessionModule(vlc);
		tlmodul.addTimeline();
		TimelineModel tlm = tlmodul.getTimelines().get(1);
		MediaObject mO = new MediaObject("C:\\Users\\EirikZimmer\\Videos\\video_test_512kb.mp4","test",MediaSourceType.VIDEO);
		tlmodul.addDisplay(0);
		//tlmodul.addDisplay(1);
		tlmodul.addTimeline();
		TimelineModel tlm2 = tlmodul.getTimelines().get(2);
		tlmodul.assignTimeline(0, tlm);
//		tlmodul.assignTimeline(1, tlm2);
		tlm.addTimelineMediaObject(0,3000,mO);
		tlm.addTimelineMediaObject(3000, 10000,mO);
		tlm2.addTimelineMediaObject(0,6000,mO);
		tlm2.addTimelineMediaObject(6000,7000,mO);
		//tlm.addTimelineMediaObject(9000,1500,mO);
		tlm.getTimelineMediaObjects().get(0).setStartPoint(5000);
		tlm.getTimelineMediaObjects().get(1).setStartPoint(10000);
		tlm2.getTimelineMediaObjects().get(0).setStartPoint(2000);
		tlmodul.playOne(0,0);
		Thread.sleep(1000);
		tlmodul.pauseOne(1);
		Thread.sleep(1000);
		tlmodul.playOne(0,1000);
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
