package modules;

/**
 * 
 * @author BEO
 * A subclass of MediaObject that stores videos and their information.
 *
 */
public class MediaObjectVideo extends MediaObject {
	
	//Start- and end points for the videoplayback.
	private int endTime;
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
	public MediaObjectVideo(String url, String name, int startTime, int endTime, int length) {
		super(url, name, startTime);
		this.endTime = endTime;
		this.length = length;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getLength() {
		return length;
	}

}
