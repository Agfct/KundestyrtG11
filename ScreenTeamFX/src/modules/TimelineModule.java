package modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;

/**
 * 
 * @author BEO
 * Singleton
 * Controls the timelines and their connections to displays. Talks to VLCController and StorageController.
 */
public class TimelineModule {
	
	private static TimelineModule timelinemodule;
	
	// Each display can have one or zero timelines
	private Dictionary<Integer, TimelineModel> displays;
	private ArrayList<TimelineModel> timelines;
	//private VLCController vlccontroller;
	//private StorageController storagecontroller;
	private float globaltime;
	private ArrayList<Event> performancestack;
	
	private TimelineModule() {
		//TODO: Implement constructor
	}
	
	public static TimelineModule getInstance(){
		if (timelinemodule == null){
			timelinemodule = new TimelineModule();
		}
		return timelinemodule;
	}
	
	public void addTimeline(TimelineModel tlm){
		timelines.add(tlm);
	}
	
	
	// TODO: We are not sure which removeTimeline to use per now.
	public void removeTimeline(int id){
		// Find the timeline in the timelines list and remove it
		for(int i=0; i<timelines.size(); i++){
			if(id==timelines.get(i).getID()){
				unassignTimeline(timelines.get(i));
				timelines.remove(i);
			}
		}
	}
	public void removeTimeline(TimelineModel tlm){
		unassignTimeline(tlm);
		timelines.remove(tlm);
	}
	
	
	public void unassignTimeline(TimelineModel tlm){
		//TODO: Go through all displays and remove the tlm timeline if it is assigned
	}
	
	public void assignTimeline(Integer display, TimelineModel tlm){
		//TODO: Check that this is legal. If so: add tlm to display in displays.
	}
	
	public void addDisplay(Integer display){
		//TODO: add display to displays, assign none (timeline)
	}
	
	public void removeDisplay(Integer display){
		//TODO: check if the display have any timelines assigned, handle it and remove the display..
	}
	
	public void playAll(){
		//TODO: first run buildPerformance, then starts running the stack
	}
	public void buildPerformance(){
		//Add all Events to list, then sort it
		performancestack = new ArrayList<Event>();
		
		for (TimelineModel timeline : timelines){
			for (MediaObject mediaobject : timeline.getMediaObjects()){
				Event event = new Event(mediaobject.getStartTime(), timeline.getID(), Action.PLAY);
				performancestack.add(event);
				if (mediaobject instanceof MediaObjectVideo){
					event = new Event(((MediaObjectVideo)mediaobject).getEndVideo(), timeline.getID(), Action.STOP);
					performancestack.add(event);
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
}
