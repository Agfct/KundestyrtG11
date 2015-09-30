package modules;

/**
 * 
 * @author BEO
 * A subclass of MediaObject that stores videos and their information.
 *
 */
public class MediaObjectVideo extends MediaObject {
	
	//Start- and end points for the videoplayback.
	private int startVideo;
	private int endVideo;
	//Length of the entire video file
	private int length;
	
	
	/**
	 * 
	 * @param url
	 * @param name
	 * @param startTime
	 * @param startVideo
	 * @param endTime
	 * @param length
	 * 
	 * TODO: How do we find the length?
	 */
	public MediaObjectVideo(String url, String name, int startTime, int startVideo, int endVideo, int length) {
		super(url, name, startTime);
		
		this.startVideo = startVideo;
		this.endVideo = endVideo;
		this.length = length;
	}

	public int getStartVideo() {
		return startVideo;
	}

	public void setStartVideo(int startVideo) {
		this.startVideo = startVideo;
	}

	public int getEndVideo() {
		return endVideo;
	}

	public void setEndVideo(int endVideo) {
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

	public int getPlayLength() {
		int result = this.endVideo - this.startVideo;
		if ( result < 0 ){
			System.out.println("MediaObjectVideo.getPlayLength: result " + result + " is less than 0!");
			return 0;
		}
		return result;
	}

}
