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
	private long start;
	private long startPoint = 0;
	private long duration;
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
	public TimelineMediaObject(long start, long duration, int timelineid, 
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

	public long getStart() {
		return start;
	}


	public void setStart(long lastObjectEnd) {
		this.start = lastObjectEnd;
	}


	public long getDuration() {
		return duration;
	}


	public void setDuration(long newDuration) {
		this.duration = newDuration;
	}


	public int getTimelineid() {
		return timelineid;
	}


	public void setTimelineid(int timelineid) {
		this.timelineid = timelineid;
	}


	public long getEnd() {
		return start+duration;
	}


	public MediaObject getParent() {
		return parent;
	}


	public void setParent(MediaObject parent) {
		this.parent = parent;
	}
	
	public long getStartPoint(){
		return startPoint;
	}

	public void setStartPoint(int spoint){
		this.startPoint = spoint;
	}
}
