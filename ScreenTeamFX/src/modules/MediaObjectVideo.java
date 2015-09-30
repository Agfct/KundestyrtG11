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
		
		//Protection endVideo
		if(endVideo>=0){
			this.endVideo = endVideo;
		}
		else {
			this.endVideo = 0;
			System.out.println("\n In MediaObjectVideo: endVideo is negative !");
		}
		
		//Protection length
		if(length>=0){
			this.length = length;
		}
		else {
			this.length = 0;
			System.out.println("\n In MediaObjectVideo: length is negative !");
		}
	}

	public int getEndVideo() {
		return endVideo;
	}

	public void setEndTime(int endVideo) {
		//Protection endVideo
		if(endVideo <= length){
			this.endVideo = endVideo;
		}
		else{
			this.endVideo = 0;
			System.out.println("\n In MediaObjectVideo: endVideo is negative ");		
		}
	}

	public int getLength() {
		return length;
	}

}
