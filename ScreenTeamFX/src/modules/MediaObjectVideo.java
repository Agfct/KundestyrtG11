package modules;

/**
 * 
 * @author BEO
 * A subclass of MediaObject that stores videos and their information.
 *
 *
 *	/////////////////////////////////////////////
 *		THIS CLASS HAS BEEN REPLACED WITH
 *		THE TimelineMediaObject CLASS
 *	/////////////////////////////////////////////
 *
 *
 */
public class MediaObjectVideo{
//	
//	//Start- and end points for the videoplayback.
//	private int startVideo;
//	private int endVideo;
//	//Length of the entire video file
//	private int length;
//	
//	
//	/**
//	 * 
//	 * @param url
//	 * @param name
//	 * @param startTime
//	 * @param startVideo
//	 * @param endTime
//	 * @param length
//	 * @param start
//	 * 
//	 * TODO: How do we find the length?
//	 */
//	public MediaObjectVideo(String url, String name, int startTime, int startVideo, int endVideo, int length) {
//		super(url, name, startTime);
//		//Protection endVideo
//		if(endVideo>=0){
//			this.endVideo = endVideo;
//		}
//		else {
//			this.endVideo = 0;
//			System.out.println("\n In MediaObjectVideo: endVideo is negative !");
//		}
//		//Protection length
//		if(length>=0){
//			this.length = length;
//		}
//		else {
//			this.length = 0;
//			System.out.println("\n In MediaObjectVideo: length is negative !");
//		}
//	}
//
//	public int getStartVideo() {
//		return startVideo;
//	}
//	/**
//	 * set at what point the video should begin
//	 * 
//	 * @param start
//	 */
//	public void setStartVideo(int startVideo) {
//		this.startVideo = startVideo;
//	}
//
//	public int getEndVideo() {
//		return endVideo;
//	}
//	
//	/**
//	 * set when the video should be stopped playing
//	 * note this is not at what point in the video it should stop
//	 * but what global time it should stop.
//	 * 
//	 * @param endVideo
//	 */
//	public void setEndVideo(int endVideo) {
//		//Protection endVideo
//		if(endVideo <= length){
//			this.endVideo = endVideo;
//		}
//		else{
//			this.endVideo = 0;
//			System.out.println("\n In MediaObjectVideo: endVideo is negative ");		
//		}
//	}
//
//	public int getLength() {
//		return length;
//	}
//
//	public int getPlayLength() {
//		int result = this.endVideo - this.startVideo;
//		if ( result < 0 ){
//			System.out.println("MediaObjectVideo.getPlayLength: result " + result + " is less than 0!");
//			return 0;
//		}
//		return result;
//	}

}
