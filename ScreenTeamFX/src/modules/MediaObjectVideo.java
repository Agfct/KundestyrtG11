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
	
	
	/**
	 * 
	 * @param url
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @param length
	 * 
	 * TODO: How do we find the length?
	 */
	public MediaObjectVideo(String url, String name, int startTime, int endVideo, int length) {
		super(url, name, startTime);
		this.endVideo = endVideo;
		this.length = length;
	}

	public int getEndVideo() {
		return endVideo;
	}

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
