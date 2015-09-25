package modules;

/**
 * 
 * @author BEO
 * A subclass of MediaObject that stores videos and their information.
 *
 */
public class MediaObjectVideo extends MediaObject {
	
	//Start- and end points for the videoplayback.
	private float startTime;
	private float endTime;
	//Length of the entire video file
	private float length;
	
	
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
	public MediaObjectVideo(String url, String name, float startTime, float endTime, float length) {
		super(url, name);
		this.startTime = startTime;
		this.endTime = endTime;
		this.length = length;
	}

	public float getStartTime() {
		return startTime;
	}

	public void setStartTime(float startTime) {
		this.startTime = startTime;
	}

	public float getEndTime() {
		return endTime;
	}

	public void setEndTime(float endTime) {
		this.endTime = endTime;
	}

	public float getLength() {
		return length;
	}

}
