package modules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import gui.*;
import vlc.VLCController;
/**
 * 
 * @author Baptiste Masselin, Eirik Z. Wold, Ole S.L. Skrede
 * Controls the timelines and their connections to displays. Talks to VLCController and StorageController.
 */
public class SessionModule implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3548566162984308320L;
	private VLCController vlccontroller;
	// Each display can have one or zero timelines
	private HashMap<Integer, TimelineModel> displays;
	private HashMap<Integer, TimelineModel> timelines;
	private ArrayList<MediaObject> mediaObjects;
	// Timer for the timeline
	private long globaltime;
	// Queue used when playing timelines
	private ArrayList<Event> performancestack;
	//counter for the id of the timelineModels
	private int tlmID;
	private boolean pausing;
	private Thread t1;
	private Thread tAll;
	
	private ArrayList<SessionListener> listeners;
	
	public SessionModule(VLCController vlc) {
		this.timelines = new HashMap<Integer,TimelineModel>();
//		this.timelines.put(0,new TimelineModel(0));
		this.mediaObjects = new ArrayList<MediaObject>();
		this.globaltime = 0;
		this.performancestack = new ArrayList<Event>();
		this.tlmID =0;
		this.displays = new HashMap<Integer,TimelineModel>();
		this.listeners = new ArrayList<SessionListener>();
		this.vlccontroller = vlc;
		this.pausing = false;
//		vlccontroller.createMediaPlayer(tlmID);
		this.t1 = new Thread();
		this.tAll = new Thread();
	}

	/**
	 * add a new timeline to the list of timelines
	 * @param tlm
	 */
	public int addTimeline(){
		tlmID +=1;
		TimelineModel tlm = new TimelineModel(tlmID);
		timelines.put(tlmID,tlm);
		vlccontroller.createMediaPlayer(tlmID);
		timelinesChanged();
		return tlmID;
	}

	// TODO: We are not sure which removeTimeline to use per now. depends on
	// what the gui knows. either id of timeline or the timelinemodel itself
	public void removeTimeline(int id){
		// Find the timeline in the timelines list and remove it
		unassignTimeline(timelines.get(id));
		timelines.remove(id);
		vlccontroller.deleteMediaPlayer(id);
		timelinesChanged();
	}
	/**
	 * goes through all displays and removes tlm if it is assigned to said display
	 * @param tlm
	 */
	public void unassignTimeline(TimelineModel tlm){
		//TODO: Go through all displays and remove the tlm timeline if it is assigned
		//Test to know that we have at least 1 display
		if(displays.isEmpty()){
		}
		else{
			//Check every displays
			for(Integer i : displays.keySet()){
				if(displays.get(i)==tlm){
					displays.put(i,null);
				}
			}		
		}
		timelineChanged(tlm);
	}
	
	/**
	 * Assigns a timeline to be played on a display
	 * @param display
	 * @param tlm the timeline that is to be assigned to the display
	 */
	public void assignTimeline(Integer display, TimelineModel tlm){
		//TODO: Change tlm to the ID for the timeline??? return something about previous assigned timeline???
		if(!displays.containsKey(display)){
			System.out.println("this display is not added to the list, please add it");
		}
		else{
			TimelineModel prevtlm = displays.put(display,tlm);
			vlccontroller.setDisplay(tlm.getID(), display);
		}
		timelineChanged(tlm);
	}
	
	/**
	 * adds a display to the list of possible displays.
	 * to be used by mainmodulecontroller if i/o module finds a new display
	 * @param display
	 */
	public void addDisplay(Integer display){
		//TODO: add display to displays, assign none (timeline)
		displays.put(display, null);
		//vlccontroller.addDisplay(Integer display);
	}
	
	/**
	 * removes a display from the list of possible displays
	 * to be used if I/O module detects that a display dissapears.
	 * @param display
	 */
	public void removeDisplay(Integer display){
		if(!displays.containsKey(display)){
			System.out.println("no such display to remove");
		}
		else{
			vlccontroller.setDisplay(displays.get(display).getID(),-1);
			displays.remove(display);
		}
	}
	
	/**
	 * first draft of playing the whole performance. this happens when
	 * the button to play all timelines is pushed.
	 * @param gbltime where the cursor is at when play all is pushed (0 if at start of the timelines)
	 */
	public void playAll(long glbtime){
		//TODO: look over and look for a better way to do this.
		//NOT DONE NBNBNBNBNBNBNBNB
		try {
			tAll.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.globaltime = glbtime;
		buildPerformance();
		tAll = allPlay(glbtime);
		pausing = false;
		tAll.start();
	}
	/**
	 * creates a thread to go through the performancestack and tell vlccontroller when and what
	 * to play and stop.
	 * @param glbtime the global point the timeline begins, 0 is start 1000 is one second in.
	 * @return
	 */
	private synchronized Thread allPlay(long glbtime){
		Thread tAll1 = new Thread(){
			public void run(){
				long startp = System.currentTimeMillis();
				long playp = System.currentTimeMillis();
				while (!performancestack.isEmpty() && pausing == false){
					playp = System.currentTimeMillis();
					if (performancestack.get(0).getTime()-glbtime<= playp-startp){
						ArrayList<Event> temp = new ArrayList<Event>();
						temp.add(performancestack.remove(0));
						while (!performancestack.isEmpty() && performancestack.get(0).getTime()==temp.get(0).getTime()){
							temp.add(performancestack.remove(0));
						}

						for (Event ev2 : temp){
							if (ev2.getAction()==Action.PLAY){
								vlccontroller.setMedia(ev2.getTimelineid(), ev2.getTimelineMediaObject().getParent().getPath());
								vlccontroller.seekOne(ev2.getTimelineid(),ev2.getTimelineMediaObject().getStartPoint());
							}
							else if(ev2.getAction()==Action.STOP){
								vlccontroller.stopOne(ev2.getTimelineid());
							}
							else if(ev2.getAction()==Action.PLAY_WITH_OFFSET){
								vlccontroller.setMedia(ev2.getTimelineid(), ev2.getTimelineMediaObject().getParent().getPath());
								long spoint = ev2.getTimelineMediaObject().getStartPoint()+ (glbtime-ev2.getTimelineMediaObject().getStart());
								vlccontroller.seekOne(ev2.getTimelineid(), spoint);
							}
						}
						try {
							vlccontroller.playAll();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//thread sleeping if its long until next event
					if (!performancestack.isEmpty() && performancestack.get(0).getTime()-glbtime> 1500+(playp-startp)){
						try {
							this.sleep((performancestack.get(0).getTime()-glbtime)-(playp-startp)-1500);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		};
		
		return tAll1;
	}
	
	
	/**
	 * Goes through all timelines on displays, get all their stacks of events and sort them based on when they 
	 * begin and end. Also check where we are on the globaltime.
	 */
	private void buildPerformance(){
		//Add all Events to list, then sort it
		performancestack = new ArrayList<Event>();
		//TODO change to only the timelines that is assigned to a display??
		// maybe for (Integer dis : displays.keyset())
		
		for(Integer dis : displays.keySet()){
			if (displays.get(dis) != null){
				for(Event ev : displays.get(dis).getTimelineStack()){
					if(ev.getTimelineMediaObject().getParent().getType()==MediaSourceType.VIDEO){
						if(ev.getAction() == Action.PLAY){
							if(ev.getTime()>=globaltime){
								performancestack.add(ev);
							}
							else if(ev.getTime()<globaltime && ev.getTimelineMediaObject().getEnd()>globaltime){
								ev.setAction(Action.PLAY_WITH_OFFSET);
								performancestack.add(ev);
							}
						}
						else if(ev.getAction()==Action.STOP){
							if (ev.getTime()>=globaltime){
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
		}
		performancestack.sort(Event.EventTimeComperator);
	}
	
	/**
	 * first draft of going through the stack and telling the vlccontroller what to do.
	 * This method runs when the user pushes play on one timeline. 
	 * @param display the display should be played
	 * @param glbtime current position of the cursor (0 if at beginning of timeline).
	 */
	public void playOne(Integer timeline, long glbtime){
		//TODO look over and look for better way to do this.
		//NOT DONE NBNNBNBNBNBNBNB
		try {
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("playing");
		this.globaltime = glbtime;
		performancestack.clear();
		ArrayList<Event> tempstack = timelines.get(timeline).getTimelineStack();
		for (Event ev2 : tempstack){
			Event ev = new Event(ev2.getTime(),ev2.getTimelineid(),ev2.getAction(),ev2.getTimelineMediaObject());
			if(ev.getTimelineMediaObject().getParent().getType()==MediaSourceType.VIDEO){
				if(ev.getAction() == Action.PLAY){
					if(ev.getTime()>=globaltime){
						performancestack.add(ev);
					}
					else if(ev.getTime()<globaltime && ev.getTimelineMediaObject().getEnd()>globaltime){
						ev.setAction(Action.PLAY_WITH_OFFSET);
						performancestack.add(ev);
					}
				}
				else if(ev.getAction()==Action.STOP){
					if (ev.getTime()>=globaltime){
						performancestack.add(ev);
					}
				}
			}
		}
		performancestack.sort(Event.EventTimeComperator);
		t1 = new Thread(){
			public void run(){
				onePlay(glbtime);
//				System.out.println("im done");
			}
		};
		pausing = false;
		t1.start();
	}
	
	/**
	 * creates a thread used for playOne that tells vlccontroller when and what to play and stop
	 * @param glbtime the startpoint of the whole program
	 *
	 */
	private void onePlay(long glbtime){
		long startp = System.currentTimeMillis();
		long playp = System.currentTimeMillis();
		while (!performancestack.isEmpty()&& pausing ==false){
			playp = System.currentTimeMillis();
			if (performancestack.get(0).getTime()-glbtime<= playp-startp && pausing ==false){
				Event ev2 = performancestack.remove(0);
				if (ev2.getAction()==Action.PLAY&& pausing ==false){
					vlccontroller.setMedia(ev2.getTimelineid(), ev2.getTimelineMediaObject().getParent().getPath());
					vlccontroller.playOne(ev2.getTimelineid(),ev2.getTimelineMediaObject().getStartPoint());
				}
				else if(ev2.getAction()==Action.STOP&& pausing ==false){
					vlccontroller.stopOne(ev2.getTimelineid());
				}
				else if(ev2.getAction()==Action.PLAY_WITH_OFFSET&& pausing ==false){
					vlccontroller.setMedia(ev2.getTimelineid(), ev2.getTimelineMediaObject().getParent().getPath());
					long spoint = ev2.getTimelineMediaObject().getStartPoint()+ (glbtime-ev2.getTimelineMediaObject().getStart());
					vlccontroller.playOne(ev2.getTimelineid(), spoint);
				}
			}
			//thread sleeping if its long until next event
			if (!performancestack.isEmpty() && performancestack.get(0).getTime()-glbtime> 1500+(playp-startp)){
				try {
//					System.out.println("night night");
					Thread.sleep((performancestack.get(0).getTime()-glbtime)-(playp-startp)-1500);
//					System.out.println("wake up");
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	/**
	 * pause all the displays and timelines.
	 */
	public void pauseAll(){
		pausing = true;
		tAll.interrupt();
		try {
			vlccontroller.pauseAll();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: Go through displays and pause timelines
	}
	
	/**
	 * pause the one timeline that is played with playOne()
	 * @param display
	 */
	public void pauseOne(Integer timelineid){
		pausing = true;
		t1.interrupt();
		vlccontroller.pauseOne(timelineid);
		System.out.println("paused");
		//TODO: Pause the timeline for this display
	}
	
	public HashMap<Integer,TimelineModel> getTimelines() {
		return timelines;
	}
	
	public void setTimelines(HashMap<Integer,TimelineModel> timelines) {
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
		
		String name = path.substring(path.lastIndexOf('\\')+1);
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
	
	public void addListener(SessionListener listener){
		listeners.add(listener);
	}
	
	public void clearListeners(){
		listeners = new ArrayList<SessionListener>();
	}
	
	public boolean removeListener(SessionListener listener){
		return listeners.remove(listener);
	}

	private void timelinesChanged() {
		for(SessionListener listener: listeners){
			listener.fireTimelinesChanged();
		}
	}
	
	private void timelineChanged(TimelineModel tlm){
		// TODO: listeners.fireTimelineChanged(tlm);
	}
	
	private void mediaObjectsChanged(){
		for(SessionListener listener: listeners){
			listener.fireMediaObjectListChanged();
		}
	}
}
