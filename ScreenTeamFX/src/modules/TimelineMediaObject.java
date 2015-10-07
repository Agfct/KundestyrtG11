package modules;

import java.io.Serializable;

/**
 * 
 * @author BEO
 *
 */
public class TimelineMediaObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4037987577895540692L;
	// Start and duration of the TimelineMediaObject, given in milliseconds.
	private int start;
	private int duration;
	private int timelineid;
	private MediaObject parent;
	
	
	/**
	 * 
	 * @param start
	 * @param duration
	 * @param timelineid
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

}
