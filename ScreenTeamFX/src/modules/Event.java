package modules;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * @author O
 * Events that happens on a timeline during a performance. An Event can be START or STOP of MediaObjects.
 */
public class Event implements Comparable<Event>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2678309540724062674L;
	/**
	 * TODO: should 'time' be from the start of the performance (00:00) or from where the it starts when
	 * 	the user presses play?
	 */
	private long time;
	private int timelineid;
	private Action action;
	private TimelineMediaObject timelineMediaObject;
	
	/**
	 * 
	 * @param time			the time that should pass before this event kicks in. Given in milliseconds. Relative to the global time and absolute to 00:00:000
	 * @param timelineid
	 * @param action
	 * @param mediaobject	The mediaobject that this event is associated with.
	 */
	public Event(long time, int timelineid, Action action, TimelineMediaObject timelineMediaObject) {
		super();
		this.time = time;
		this.timelineid = timelineid;
		this.action = action;
		this.setTimelineMediaObject(timelineMediaObject);
	}

	public long getTime() {
		return time;
	}

	public void setTime(int time) {
		if (time < 0){
			System.out.println("Tried to set Event time to "+time+". Ignored this and did not update Event.time.");
			return;
		}
		this.time = time;
	}

	public int getTimelineid() {
		return timelineid;
	}

	public void setTimelineid(int timelineid) {
		this.timelineid = timelineid;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public int compareTo(Event otherEvent) {
		
		long otherTime = otherEvent.getTime();
		
		/**
		 * If one MediaObject stops and another one starts at the same time and on the same Timeline,
		 * we must stop the old before starting the new one (?).
		 */
		if (otherTime == this.time){
			// Check if the Events are on the same timeline
			int otherTimelineid = otherEvent.getTimelineid();
			if (otherTimelineid == this.timelineid){
				// Check if one of them is a STOP action and return a value accordingly
				if (this.action == Action.STOP && otherEvent.getAction() == Action.PLAY){
					// This Event should happen first
					return -1;
				}
				else if (this.action == Action.PLAY && otherEvent.getAction() == Action.STOP){
					// The other Event should happen first
					return 1;
				}
				// else: both are PLAY or both are STOP, and the order should not matter
			}
		}
		
		return (int) (this.time - otherTime);
		
		// Descending order
		// return compareTime - this.time;
	}
	
	public TimelineMediaObject getTimelineMediaObject() {
		return timelineMediaObject;
	}

	public void setTimelineMediaObject(TimelineMediaObject timelineMediaObject) {
		this.timelineMediaObject = timelineMediaObject;
	}

	public static Comparator<Event> EventTimeComperator = new Comparator<Event>() {
		
		public int compare(Event event1, Event event2){
			
			// ascending order
			return event1.compareTo(event2);
			
			// descending order
			// return event2.getTime().compareTo(event1.getTime());
		}
	};
}