package modules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class TimelineModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1075691266821671733L;
	private ArrayList<TimelineMediaObject> timelineMediaObjects;
	private final int id;
	private ArrayList<Event> timelineStack;
	
	private ArrayList<Integer> assignedDisplays;
	
	private String nameOfTimeline;
	private boolean muted;
	private boolean hidden; 
	
	public TimelineModel(int id) {
		super();
		this.timelineMediaObjects = new ArrayList<TimelineMediaObject>();
		this.id = id;
		this.timelineStack = new ArrayList<Event>();
		this.assignedDisplays=new ArrayList<Integer>();
		this.nameOfTimeline= "Timeline " + id;
		this.muted = true;
		this.hidden = false;
		
	}

	public int getID() {
		return id;
	}
	
	public void pressMuteButton(){
		muted = !muted;
	}
	public boolean getMuted(){
		return muted;
	}
	
	public void pressHideButton(){
		hidden = !hidden;
	}
	public boolean getHidden(){
		return hidden;
	}
	
	public ArrayList<Event> getTimelineStack(){
		return timelineStack;
	}
	
	/**
	 * Add a new timelineMediaObject to the timeline. Should always keep them sorted, so that it is easy to 
	 * avoid overlapping TimelineMediaObjects. The method only puts the TimelineMediaObject on the timeline
	 * if there is available space where the TimelineMediaObject starts, the duration may be shorted down
	 * to make it fit. The return string says something about the result of trying to fit in the 
	 * TimelineMediaObject.
	 * user friendly.
	 * @param m
	 * @return
	 */
	public String addTimelineMediaObject(TimelineMediaObject m){
		
		// If the timeline is empty, we can just add the TimelineMediaObject without worrying about sorting
		if (timelineMediaObjects.size() == 0) {
			timelineMediaObjects.add(m);
			timelinechanged();
			return "TimelineMediaObject added with full length"; 
		}
		
		// If the TimelineMediaObject has the same start as an already existing one, abort without adding it
		for (int i=0; i<timelineMediaObjects.size(); i++){
			if ( m.getStart() == timelineMediaObjects.get(i).getStart() ) {
				return "TimelineMediaObject was not added, there was no space at the given position";
			}
		}
		
		// Check if we can put m on the start of the timeline, in which case we don't need to consider earlier objects
		if ( m.getStart() < timelineMediaObjects.get(0).getStart() ) {
			// Do not need to consider any objects earlier on the timeline, since there are none
			timelineMediaObjects.add(0, m);
					
			// But check that the duration of m do not overlap with the next object
			if ( m.getStart()+m.getDuration() < timelineMediaObjects.get(1).getStart() ) {
				// Everyting is okay
				timelinechanged();
				return "TimelineMediaObject added with full length";
			}
			else {
				// Need to squeeze m's duration to fit it in
				long newDuration = timelineMediaObjects.get(1).getStart() - m.getStart();
				m.setDuration(newDuration);
				timelinechanged();
				return "TimelineMediaObject added with reduced duration";
			}
		}
		
		// Check if we can put in m at the end
		if ( timelineMediaObjects.get(timelineMediaObjects.size()-1).getStart() < m.getStart() ) {
			// Might need to delay the start of m
			int lastObjectIndex = timelineMediaObjects.size() - 1;
			long lastObjectEnd = timelineMediaObjects.get(lastObjectIndex).getStart() + timelineMediaObjects.get(lastObjectIndex).getDuration();
			if ( m.getStart() < lastObjectEnd ) {
				m.setStart(lastObjectEnd);
				timelineMediaObjects.add(m);
				timelinechanged();
				return "TimelineMediaObject added but with delayed start";
			}
			// Else we did not need to delay the start
			timelineMediaObjects.add(m);
			timelinechanged();
			return "TimelineMediaObject added with full length";
		}
		
		/*
		 * m wants to go between two other objects on the timeline. Find out where
		 * and check if there is any space
		 */
		for (int i=1; i<timelineMediaObjects.size(); i++){
			if ( m.getStart() < timelineMediaObjects.get(i).getStart() ) {
				// Check if there is any space between timelineMediaobject (i-1) and (i)
				if ( timelineMediaObjects.get(i-1).getStart()+timelineMediaObjects.get(i-1).getDuration() < timelineMediaObjects.get(i).getStart() ) {
					// There is space, but might have to delay the start
					if ( m.getStart() < timelineMediaObjects.get(i-1).getStart()+timelineMediaObjects.get(i-1).getDuration() ) {
						m.setStart(timelineMediaObjects.get(i-1).getStart()+timelineMediaObjects.get(i-1).getDuration());
						
						// Might also have to reduce the duration to fit m in
						if ( timelineMediaObjects.get(i).getStart() < m.getStart()+m.getDuration() ) {
							long newDuration = timelineMediaObjects.get(i).getStart() - m.getStart(); 
							m.setDuration(newDuration);
							timelineMediaObjects.add(i, m);
							timelinechanged();
							return "TimelineMediaObject added with delayed start and reduced duration";
						}
						else {
							timelineMediaObjects.add(i, m);
							timelinechanged();
							return "TimelineMediaObject added with delayed start";
						}
					}
					else {
						// Start is fine, but need to check duration
						if ( timelineMediaObjects.get(i).getStart() < m.getStart()+m.getDuration() ) {
							long newDuration = timelineMediaObjects.get(i).getStart() - m.getStart(); 
							m.setDuration(newDuration);
							timelineMediaObjects.add(i, m);
							timelinechanged();
							return "TimelineMediaObject added with reduced duration";
						}
						
						// Nope, both start and duration is fine!
						timelineMediaObjects.add(i, m);
						timelinechanged();
						return "TimelineMediaObject added with full length";
					}
				}
				else {
					// There is no space, so we can not add m
					return "TimelineMediaObject was not added, there was no space at the given position";
				}
			}
		}
		
		return "Something went wrong. Could not add TimelineMediaObject";
		
	}
	
	public void addTimelineMediaObject(long start, long dur, MediaObject parent){
		TimelineMediaObject temp = new TimelineMediaObject(start,dur,this.id,parent);
		
		timelineMediaObjects.add(temp);
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
	 * Try to change TimelineMediaObject to the new start or new duration
	 * @param tlmo: the timelinemediaobject that has changed
	 * @param newStart: the new startpoint of the tlmo
	 * @param newInternalStart: new offset for the tlmo
	 * @param newDuration: new duration of the tlmo
	 * @return String message of what happened
	 */
	public String timelineMediaObjectChanged( TimelineMediaObject tlmo, int newStart, int newInternalStart, int newDuration){	
		if(!timelineMediaObjects.remove(tlmo)){
			return "TimelineMediaObject not found on timeline";
		}
		
		TimelineMediaObject newTimelineMediaObject = new TimelineMediaObject(newStart, newInternalStart, newDuration, tlmo.getTimelineid(), tlmo.getParent());
		
		// Check window collision
		if(MainModuleController.getInstance().getSession().checkWindowCollision(newTimelineMediaObject)){
			// Collision detected. Add back the old timelineMediaObject
			this.addTimelineMediaObject(tlmo);
			return "Window would collide in new position, window not moved";
		}
		
		String result = this.addTimelineMediaObject(newTimelineMediaObject);
		this.removeTimelineMediaObject(newTimelineMediaObject);
		// Check the result, if the new one was not added, put back the old one
//		tlmo.
		if (result.equals("TimelineMediaObject was not added, there was no space at the given position") ){
			result = this.addTimelineMediaObject(tlmo);
			if (result.equals("TimelineMediaObject was not added, there was no space at the given position") ){
				return "Something went wrong, could not change the TimelineMediaObject, and it was removed from the timeline";
			}
			return "Could not modify the TimelineMediaObject, it remains unchanged";
		}
		//Since the if above did not fire, we know the changed are legal. make changes
		tlmo.setStart(newStart);
		tlmo.setStartPoint(newInternalStart);
		tlmo.setDuration(newDuration);
		result=this.addTimelineMediaObject(tlmo);
		timelinechanged();
		return "TImelineMediaObject successfully modified. " + result;
	}
	
	/**
	 * goes through all mediaObjects in the timeline and creates the new stack for the sessionbuilder to use.
	 * 
	 */
	public void timelinechanged(){
		timelineStack.clear();
		TimelineMediaObject mO;
		Event start;
		Event end;
		for(int i=0;i<timelineMediaObjects.size();i++){
			mO = timelineMediaObjects.get(i);
			if (mO.getParent().getType()==MediaSourceType.VIDEO){
				start = new Event(mO.getStart(), id, Action.PLAY, mO);
				end = new Event(mO.getEnd(), id, Action.STOP, mO);
				timelineStack.add(start);
				timelineStack.add(end);
			}
			else if (mO.getParent().getType()==MediaSourceType.AUDIO){
				start = new Event(mO.getStart(), id, Action.PLAY, mO);
				end = new Event(mO.getEnd(), id, Action.STOP, mO);
				timelineStack.add(start);
				timelineStack.add(end);
			}
			else if(mO.getParent().getType()==MediaSourceType.WINDOW){
				start = new Event(mO.getStart(),id,Action.SHOW,mO);
				end = new Event(mO.getEnd(), id, Action.HIDE, mO);
				timelineStack.add(start);
				timelineStack.add(end);
			}
			else if(mO.getParent().getType()==MediaSourceType.IMAGE){
				start = new Event(mO.getStart(), id, Action.PLAY, mO);
				end = new Event(mO.getEnd(), id, Action.STOP, mO);
				timelineStack.add(start);
				timelineStack.add(end);
			}
		}
		Collections.sort(timelineStack);
	}

	public ArrayList<Integer> getAssignedDisplays() {
		return assignedDisplays;
	}
	
	public void addDisplay(Integer dispID){
		assignedDisplays.add(dispID);
	}
	
	public void removeDisplay(Integer dispID){
		if (assignedDisplays.contains(dispID)){
			assignedDisplays.remove(dispID);			
		}
	}

	public void removeAllDisplays() {
		assignedDisplays = new ArrayList<Integer>(); 
	}
	
	public String getNameOfTimeline(){
		return nameOfTimeline;
	}
	/**
	 * Sets the name of the timeline.
	 * Rejects the request if name is above 50 chars
	 * NB: this is not proper MVC as it should run fireTimelineChanged() in sessionmodule. It does however not affect any user exp
	 * @param s: new name of the timeline
	 * @return boolean if the length of s is valid
	 */
	public boolean setNameOfTimeline(String s){
		if(s.length()<50){
			nameOfTimeline=s;
			return true;
			
		}
		return false;
	}
		
}
