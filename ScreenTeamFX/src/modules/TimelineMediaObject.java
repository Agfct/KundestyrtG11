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


	public void setStart(long newStart) {
		if(newStart>=0){
			this.start = newStart;
		}
		
	}


	public long getDuration() {
		return duration;
	}

	/**
	 * Checks if the duration is valid within both the total length of parent and remaining time after startpoint
	 * @param newDuration
	 */
	public void setDuration(long newDuration) {
		if(newDuration>0){
			long maxDur=parent.getLength()-startPoint;
			this.duration=Math.min(maxDur, newDuration);
		}
		
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
	
	public void setStartPoint(long spoint){
		if(spoint < 0){
			return;
		}
		if( parent.getLength() <= startPoint ){
			return;
		}
		this.startPoint = spoint;
		if( parent.getLength() < spoint + duration ){
			this.duration = parent.getLength() - spoint;
		}
	}
}
