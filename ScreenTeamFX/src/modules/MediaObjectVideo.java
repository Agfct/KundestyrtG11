package modules;

/**
 * 
 * @author BEO
 * A subclass of MediaObject that stores videos and their information.
 *
 */
public class MediaObjectVideo extends MediaObject {
	
	//Start- and end points for the videoplayback.
	private int endVideo;
	//Length of the entire video file
	private int length;
	//point in video to begin
	private int startVideo;
	
	
	/**
	 * 
	 * @param url
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @param length
	 * @param start
	 * 
	 * TODO: How do we find the length?
	 */
	public MediaObjectVideo(String url, String name, int startTime, int endVideo, int length,int start) {
		super(url, name, startTime);
		this.endVideo = endVideo;
		this.length = length;
		this.startVideo = start;
	}

	public int getEndVideo() {
		return endVideo;
	}
	/**
	 * set at what point the video should begin
	 * 
	 * @param start
	 */
	public void setstartVideo(int start){
		startVideo= start;
	}
	/**
	 * set when the video should be stopped playing
	 * note this is not at what point in the video it should stop
	 * but what global time it should stop.
	 * 
	 * @param endVideo
	 */
	public void setEndTime(int endVideo) {
		if(endVideo <= length){
			this.endVideo = endVideo;
		}
		else{
			System.out.println("\n Length of the video inferior of the endVideo ");		
		}
	}

	public int getLength() {
		return length;
	}

}
