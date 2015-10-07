package modules;

/**
 * 
 * @author BEO
 *
 */
public class TimelineMediaObject {
	
	// Start and duration of the TimelineMediaObject, given in milliseconds.
	private int start;
	private int startPoint = 0;
	private int duration;
	private int timelineid;
	private MediaObject parent;
	
	
	/**
	 * 
	 * @param start			when on timeline this object should start
	 * @param duration		length this object should play
	 * @param timelineid	id of the timeline this object belongs to
	 * @param parent		pointer to a MediaObject, with info about the video (path and length)
	 * 
	 */
	public TimelineMediaObject(int start, int duration, int timelineid, 
								MediaObject parent){
		this.start = start;
		this.duration = duration;
		this.timelineid = timelineid;
		this.parent = parent;
	}

	/**
	 * 
	 * @param start			when on timeline this object should start
	 * @param spoint		what point in the video, the video should start at
	 * @param duration		length this object should play
	 * @param timelineid	id of the timeline this object belongs to
	 * @param parent		pointer to a MediaObject, with info about the video (path and length)
	 */
	public TimelineMediaObject(int start, int spoint, int duration, int timelineid, 
			MediaObject parent){
			this.start = start;
			this.startPoint = spoint;
			this.duration = duration;
			this.timelineid = timelineid;
			this.parent = parent;
		}

	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getDuration() {
		return duration;
	}


	public void setDuration(int duration) {
		this.duration = duration;
	}


	public int getTimelineid() {
		return timelineid;
	}


	public void setTimelineid(int timelineid) {
		this.timelineid = timelineid;
	}


	public int getEnd() {
		return start+duration;
	}


	public MediaObject getParent() {
		return parent;
	}


	public void setParent(MediaObject parent) {
		this.parent = parent;
	}
	
	public int getStartPoint(){
		return startPoint;
	}

	public void setStartPoint(int spoint){
		this.startPoint = spoint;
	}
}
