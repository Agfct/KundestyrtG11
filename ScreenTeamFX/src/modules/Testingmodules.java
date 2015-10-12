package modules;

import vlc.VLCController;

public class Testingmodules {
	private VLCController vlc;
	private TimelineModule tlmodul;
	
	
	public Testingmodules(){
		this.vlc = new VLCController("C:\\Program Files\\VideoLAN\\VLC64");
		tlmodul = new TimelineModule(vlc);
		TimelineModel tlm = tlmodul.getTimelines().get(0);
		MediaObject mO = new MediaObject("C:\\Users\\EirikZimmer\\Videos\\video_test_512kb.mp4","test",MediaSourceType.VIDEO);
		
		tlm.addTimelineMediaObject(0, 10, mO);
		tlm.addTimelineMediaObject(11,19,mO);
	}
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub

	}

}
