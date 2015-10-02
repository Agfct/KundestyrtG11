package modules;

import java.util.ArrayList;
import java.util.Collections;

public class TimelineModel {
	
	private ArrayList<TimelineMediaObject> timelineMediaObjects;
	private final int id;
	private ArrayList<Event> timelineStack;
	public TimelineModel(ArrayList<TimelineMediaObject> timelineMediaObjects, int id) {
		super();
		this.timelineMediaObjects = timelineMediaObjects;
		this.id = id;
		this.timelineStack = new ArrayList<Event>();
	}

	public int getID() {
		return id;
	}
	
	public ArrayList<Event> getTimelineStack(){
		return timelineStack;
	}
	/**
	 * add a new timelineMediaObject to the timeline
	 * 
	 * @param m
	 */
	public void addTimelineMediaObject(TimelineMediaObject m){
		timelineMediaObjects.add(m);
		timelinechanged();
	}
	/**
	 * removes a timelineMediaObject from the timeline
	 * 
	 * @param m
	 */
	public void removeTimelineMediaObject(TimelineMediaObject m){
		timelineMediaObjects.remove(m);
		timelinechanged();
	}
	
	public ArrayList<TimelineMediaObject> getTimelineMediaObjects(){
		return timelineMediaObjects;
	}
	
	/**
	 * goes through all mediaObjects in the timeline and creates the new stack
	 * 
	 */
	public void timelinechanged(){
		timelineStack.clear();
		TimelineMediaObject mO;
		Event start;
		Event end;
		for(int i=0;i>=timelineMediaObjects.size();i++){
			mO = timelineMediaObjects.get(i);
			start = new Event(mO.getStart(), id, Action.PLAY, mO);
			end = new Event(mO.getEnd(), id, Action.STOP, mO);
			timelineStack.add(start);
			timelineStack.add(end);
			Collections.sort(timelineStack);
		}
	}
		
}

