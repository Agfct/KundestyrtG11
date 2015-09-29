package modules;

import java.util.Comparator;

/**
 * 
 * @author O
 * Events that happens on a timeline during a performance. An Event can be START or STOP of MediaObjects.
 */
public class Event implements Comparable<Event>{
	/**
	 * TODO: should 'time' be from the start of the performance (00:00) or from where the it starts when
	 * 	the user presses play?
	 */
	private int time;
	private int timelineid;
	private Action action;
	
	/**
	 * 
	 * @param time			the time that should pass before this event kicks in. Given in milliseconds.
	 * @param timelineid
	 * @param action
	 */
	public Event(int time, int timelineid, Action action) {
		super();
		this.time = time;
		this.timelineid = timelineid;
		this.action = action;
	}

	public int getTime() {
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
		
		int otherTime = otherEvent.getTime();
		
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
		
		return this.time - otherTime;
		
		// Descending order
		// return compareTime - this.time;
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