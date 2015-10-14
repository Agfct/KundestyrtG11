package modules;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author BEO
 * Singleton
 * Controls the timelines and their connections to displays. Talks to VLCController and StorageController.
 */
public class SessionModule {
	//private VLCController vlccontroller;
	// Each display can have one or zero timelines
	private HashMap<Integer, TimelineModel> displays;
	private ArrayList<TimelineModel> timelines;
	private ArrayList<MediaObject> mediaObjects;
	// Timer for the timeline
	private int globaltime;
	// Queue used when playing timelines
	private ArrayList<Event> performancestack;
	//counter for the id of the timelineModels
	private int tlmID;
	
	private ArrayList<Object> listeners;
	// TODO: Would be better (more correct) to use Bean or create a listener interface for the listeners!
	
	public SessionModule() {
		this.timelines = new ArrayList<TimelineModel>();
		this.timelines.add(new TimelineModel(0));
		this.mediaObjects = new ArrayList<MediaObject>();
		this.globaltime = 0;
		this.performancestack = new ArrayList<Event>();
		this.tlmID =0;
		this.displays = new HashMap<Integer,TimelineModel>();
		this.listeners = new ArrayList<Object>();
	}

	/**
	 * add a new timeline to the list of timelines
	 * @param tlm
	 */
	public int addTimeline(){
		tlmID +=1;
		TimelineModel tlm = new TimelineModel(tlmID);
		timelines.add(tlm);
		timelinesChanged();
		return tlmID;
	}

	// TODO: We are not sure which removeTimeline to use per now. depends on
	// what the gui knows. either id of timeline or the timelinemodel itself
	public void removeTimeline(int id){
		// Find the timeline in the timelines list and remove it
		for(int i=0; i<timelines.size(); i++){
			if(id==timelines.get(i).getID()){
				unassignTimeline(timelines.get(i));
				timelines.remove(i);
			}
		}
		timelinesChanged();
	}
	
	public void unassignTimeline(TimelineModel tlm){
		//TODO: Go through all displays and remove the tlm timeline if it is assigned
		int i;
		//Test to know that we have at least 1 display
		if(displays.isEmpty()){
		}
		else{
			//Check every displays
			for(i=0; i<displays.size(); i++){
				if(displays.get(i)==tlm){
					displays.remove(i);
				}
			}		
		}
		timelineChanged(tlm);
	}
	
	public void assignTimeline(Integer display, TimelineModel tlm){
		//TODO: Check that this is legal. If so: add tlm to display in displays.
		if(displays.isEmpty()){
		}
		else{
			TimelineModel prevtlm = displays.put(display,tlm);
		}
		timelineChanged(tlm);
	}
	
	// !!!! I put Timeline Model because I can't create a new display without TimelineModel
	public void addDisplay(Integer display){
		//TODO: add display to displays, assign none (timeline)
		displays.put(display, null);
	}
	
	public void removeDisplay(Integer display){
		//TODO: check if the display have any timelines assigned, handle it and remove the display..
		if(displays.isEmpty()){
		}
		else{
			displays.remove(display);
		}
	}
	
	public void playAll(){
		//TODO: first run buildPerformance, then starts running the stack
	}
	
	/**
	 * Goes through all timelines on displays, get all their stacks of events and sort them based on when they 
	 * begin and end. Also check where we are on the globaltime.
	 */
	public void buildPerformance(){
		//Add all Events to list, then sort it
		performancestack = new ArrayList<Event>();
		//TODO change to only the timelines that is assigned to a display??
		// maybe for (Integer dis : displays.keyset())
		
		for(Integer dis : displays.keySet()){
			for(Event ev : displays.get(dis).getTimelineStack()){
				if(ev.getTimelineMediaObject().getParent().getType()==MediaSourceType.VIDEO){
					if(ev.getAction() == Action.PLAY){
						if(ev.getTime()>globaltime){
							performancestack.add(ev);
						}
						else if(ev.getTime()<globaltime && ev.getTimelineMediaObject().getEnd()>globaltime){
							ev.setAction(Action.PLAY_WITH_OFFSET);
							performancestack.add(ev);
						}
					}
					else if(ev.getAction()==Action.STOP){
						if (ev.getTime()>globaltime){
							performancestack.add(ev);
						}
					}
				}
				else if(ev.getTimelineMediaObject().getParent().getType()==MediaSourceType.STREAM){
					/**
					 * TODO: Handle streams here. (Do they have both a start and end time? Might want to change between
					 * two streams on one timeline??
					 */
					System.out.println("Adding events for streams is not implemented in TimelineModule.java: buildPerformance() yet.");
				}
			}
		}
		performancestack.sort(Event.EventTimeComperator);
	}
	
	public void playOne(Integer display){
		//TODO: Play the timeline for this display
	}
	
	public void pauseAll(){
		//TODO: Go through displays and pause timelines
	}
	
	public void pauseOne(Integer display){
		//TODO: Pause the timeline for this display
	}
	
	public ArrayList<TimelineModel> getTimelines() {
		return timelines;
	}
	
	public void setTimelines(ArrayList<TimelineModel> timelines) {
		this.timelines = timelines;
		timelinesChanged();
	}
	
	/**
	 * Creates a new MediaObject and stores it in the this SessionModule. If a MediaObject with the same path already
	 * exists, the method will not create a new MediaObject, but return the existing one.
	 * @param mst
	 * @param path
	 */
	public MediaObject createNewMediaObject(MediaSourceType mst, String path){
		
		// Check if this MediaObject is already stored in the list, by comparing paths
		for (int i=0; i<mediaObjects.size(); i++){
			if (mediaObjects.get(i).getPath().equals(path)) {
				// Return the old MediaObject with equal path
				return mediaObjects.get(i);
			}
		}
		
		// Did not find an old MediaObject with equal path, so create a new one
		String name = path.substring(path.lastIndexOf('/')+1);
		MediaObject mo = new MediaObject(path, name, mst);
		mediaObjects.add(mo);
		mediaObjectsChanged();
		return mo;
	}

	public ArrayList<MediaObject> getMediaObjects() {
		return this.mediaObjects;
	}
	
	/**
	 * Adds a new TimelineMediaObject to the specified TimelineModel, based on the MediaObject and the startTime.
	 * @param mediaObject
	 * @param timeline
	 * @param startTime
	 */
	public String addMediaObjectToTimeline(MediaObject mediaObject, TimelineModel timeline, int startTime){
		TimelineMediaObject tlmo = new TimelineMediaObject(startTime, mediaObject.getLength(), timeline.getID(), mediaObject);
		String result = timeline.addTimelineMediaObject(tlmo);
		timelineChanged(timeline);
		return result;
	}
	
	public String timelineMediaObjectChanged(TimelineModel tlm, TimelineMediaObject tlmo, int newStart, int newInternalStart, int newDuration){
		String result = tlm.timelineMediaObjectChanged(tlmo, newStart, newInternalStart, newDuration);
		timelineChanged(tlm);
		return result;
	}
	
	public void addListener(Object listener){
		listeners.add(listener);
	}
	
	public void clearListeners(){
		listeners = new ArrayList<Object>();
	}
	
	public boolean removeListener(Object listener){
		return listeners.remove(listener);
	}

	private void timelinesChanged() {
		// TODO listeners.fireTimelinesChanged();
	}
	
	private void timelineChanged(TimelineModel tlm){
		// TODO: listeners.fireTimelineChanged(tlm);
	}
	
	private void mediaObjectsChanged(){
		// TODO: listeners.mediaObjectListChanged();
	}
}
