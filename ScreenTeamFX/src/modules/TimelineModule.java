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
	private int globaltime;
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
	@SuppressWarnings("unused")
	public void buildPerformance(){
		//Add all Events to list, then sort it
		performancestack = new ArrayList<Event>();
		
		for (TimelineModel timeline : timelines){
			for (MediaObject mediaobject : timeline.getMediaObjects()){
				// Videos have start and stop time, // TODO: Streams might be handled differently
				if (mediaobject instanceof MediaObjectVideo){
					// If the video starts after the globaltime, add both an PLAY and STOP Event
					if (mediaobject.getStartTime() > globaltime){
						Event event = new Event(mediaobject.getStartTime(), timeline.getID(), Action.PLAY, mediaobject);
						performancestack.add(event);
						
						int eventtime = mediaobject.getStartTime()+((MediaObjectVideo)mediaobject).getEndVideo();
						event = new Event(eventtime, timeline.getID(), Action.STOP, mediaobject);
						performancestack.add(event);
					}
					// If the globaltime is between the start and stop of the video, we need both PLAY and STOP, but should start video at globaltime+startVideo
					else if ( ((MediaObjectVideo)mediaobject).getStartTime() < globaltime 
							&& globaltime < (((MediaObjectVideo)mediaobject).getStartTime())+((MediaObjectVideo)mediaobject).getPlayLength() ){
						Event event = new Event(globaltime, timeline.getID(), Action.PLAY_WITH_OFFSET, mediaobject);
						performancestack.add(event);
						
						int eventtime = mediaobject.getStartTime()+((MediaObjectVideo)mediaobject).getEndVideo();
						event = new Event(eventtime, timeline.getID(), Action.STOP, mediaobject);
						performancestack.add(event);
					}
					// Else: the video stops before globaltime, so no need to do anything.
				}
				else {
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
}
