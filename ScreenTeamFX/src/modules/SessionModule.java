package modules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gui.*;
import vlc.VLCController;
import vlc.VLCMediaPlayer;
/**
 * 
 * @author Baptiste Masselin, Eirik Z. Wold, Ole S.L. Skrede, Magnus Gundersen
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
	private boolean paused;
	private Thread t1;
	private Thread tAll;
	
	private Thread globalTimeTicker;
	
	private ArrayList<SessionListener> listeners;
	private ArrayList<Integer> timelineOrder;
	
	// Constant used when creating TimelineMediaObjects that are images. Used as a reasonable duration when first appearing on a timeline.
	private final long IMAGE_DURATION = 30000;
	
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
		this.pausing = true;
		this.paused = true;
//		vlccontroller.createMediaPlayer(tlmID);
		this.t1 = new Thread();
		this.tAll = new Thread();
		this.globalTimeTicker = new Thread();
		this.timelineOrder=new ArrayList<Integer>();
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
		timelineOrder.add(0,tlm.getID()); //Added the timeLine to the beginning of the list. This means the new timeline will be at the first positision in the gui
		timelineChanged(TimeLineChanges.ADDED, tlm);
		return tlmID;
	}

	/*
	 * This function removes the timeline from the modules.
	 * It then sends the removed object back to the GUI, in order for all pointers to the timeline to be removed. 
	 */
	public void removeTimeline(int id){
		// Find the timeline in the timelines list and remove it
		TimelineModel tlm = timelines.get(id);
		unassignTimeline(tlm);
		timelines.remove(id);
		vlccontroller.deleteMediaPlayer(id);
		timelineOrder.remove(new Integer(id));
		timelineChanged(TimeLineChanges.REMOVED,tlm );
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
					vlccontroller.unassignDisplay(tlm.getID());
					tlm.removeDisplay(i);
				}
			}		
		}
		timelineChanged(TimeLineChanges.MODIFIED, tlm);
	}
	
	/**
	 * Assigns a timeline to be played on a display
	 * @param display
	 * @param tlm the timeline that is to be assigned to the display
	 */
	public void assignTimeline(Integer display, TimelineModel tlm){
		if(!displays.containsKey(display)){
			System.out.println("this display is not added to the list, please add it");
		}
		else{
			TimelineModel prevtlm = displays.put(display,tlm);
			tlm.addDisplay(display);
			if(prevtlm !=null){
				prevtlm.removeDisplay(display);
				vlccontroller.unassignDisplay(prevtlm.getID());
				timelineChanged(TimeLineChanges.MODIFIED, prevtlm);
			}
			vlccontroller.assignDisplay(tlm.getID(), display);
		}
		
		timelineChanged(TimeLineChanges.MODIFIED,tlm);
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
	
	public void updateDisplays(ArrayList<Integer> displays){
		for(Integer d : displays){
			if(!this.displays.containsKey(d)){
				addDisplay(d);
			}
			
		}
		for(Integer i : this.displays.keySet()){
			if(!displays.contains(i)){
				removeDisplay(i);
			}
		}
		vlccontroller.updateDisplays(displays);
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
			vlccontroller.unassignDisplay(displays.remove(display).getID());
		}
	}
	
	/**
	 * first draft of playing the whole performance. this happens when
	 * the button to play all timelines is pushed.
	 * @param gbltime where the cursor is at when play all is pushed (0 if at start of the timelines)
	 */
	public void playAll(){
		if(paused){			
			System.out.println("PLAYALL");
			paused = false;
			try {
				tAll.join();
				globalTimeTicker.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			buildPerformance();
			tAll = allPlay(globaltime);
			globalTimeTicker=tickGlobalTime(globaltime);
			pausing = false;
			tAll.start();
//			globalTimeTicker.start();
		}
	}
	
	private synchronized Thread tickGlobalTime(long globalTimeAtStart){
		Thread globalTicker = new Thread(){
			public void run(){
				long startp = System.currentTimeMillis();
				long playp = System.currentTimeMillis();
				while(!pausing){
					playp = System.currentTimeMillis();
					globaltime=globalTimeAtStart+playp-startp;
					globalTimeChanged();
					try {
						this.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
					
				}
				
				// Run update one last time before the thread dies. 
				playp = System.currentTimeMillis();
				globaltime=globalTimeAtStart+playp-startp;
				globalTimeChanged();
			}
		};
		return globalTicker;
	}


	/**
	 * creates a thread to go through the performancestack and tell vlccontroller when and what
	 * to play and stop.
	 * @param glbtime the global point the timeline begins, 0 is start 1000 is one second in.
	 * @return
	 */
	private synchronized Thread allPlay(long glbtime){
		System.out.println();
		Thread tAll1 = new Thread(){
			public void run(){
				System.out.println("RUN ALLPLAY");
				System.out.println(performancestack.size());
				long startp = System.currentTimeMillis();
				long playp = System.currentTimeMillis();
				Map<Integer,Long> pplay = new HashMap<Integer,Long>();
				while (!performancestack.isEmpty() && pausing == false){
					playp = System.currentTimeMillis();
					if (performancestack.get(0).getTime()-glbtime<= playp-startp){
						ArrayList<Event> temp = new ArrayList<Event>();
						temp.add(performancestack.remove(0));
						pplay.clear();
						while (!performancestack.isEmpty() && performancestack.get(0).getTime()-glbtime<=playp-startp){
							temp.add(performancestack.remove(0));
						}
						for (Event ev2 : temp){
							if (ev2.getAction()==Action.PLAY){
								vlccontroller.setMedia(ev2.getTimelineid(), ev2.getTimelineMediaObject().getParent().getPath());
								pplay.put(ev2.getTimelineid(), ev2.getTimelineMediaObject().getStartPoint());
								vlccontroller.seekOne(ev2.getTimelineid(),ev2.getTimelineMediaObject().getStartPoint());
								
							}
							else if(ev2.getAction()==Action.STOP){
								vlccontroller.stopOne(ev2.getTimelineid());
							}
							else if(ev2.getAction()==Action.PLAY_WITH_OFFSET){
								if (vlccontroller.getMediaPlayerList().get(ev2.getTimelineid()).getMediaPath()!=ev2.getTimelineMediaObject().getParent().getPath()){
									vlccontroller.setMedia(ev2.getTimelineid(), ev2.getTimelineMediaObject().getParent().getPath());
								}
								long spoint = ev2.getTimelineMediaObject().getStartPoint()+ (glbtime-ev2.getTimelineMediaObject().getStart());
								pplay.put(ev2.getTimelineid(), spoint);
								vlccontroller.seekOne(ev2.getTimelineid(), spoint);
							}
						}
						try {
							vlccontroller.SeekMultiple(pplay);
							vlccontroller.playAll();
							if (!globalTimeTicker.isAlive()){
								globalTimeTicker.start();
							}
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
		System.out.println("BUILD PERFORMANCE");
		//Add all Events to list, then sort it
		performancestack = new ArrayList<Event>();
		//TODO change to only the timelines that is assigned to a display??
		// maybe for (Integer dis : displays.keyset())
		System.out.println("Displays: "+ displays.keySet());
		for(Integer dis : displays.keySet()){
			if (displays.get(dis) != null){
				for(Event ev2 : displays.get(dis).getTimelineStack()){
					Event ev = new Event(ev2.getTime(), ev2.getTimelineid(), ev2.getAction(), ev2.getTimelineMediaObject());
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
	public void playOne(Integer timeline){
		if(pausing){
			try {
				t1.join();
				globalTimeTicker.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("playing");
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
					onePlay(globaltime);
//				System.out.println("im done");
				}
			};
			globalTimeTicker=tickGlobalTime(globaltime);
			pausing = false;
			t1.start();
			globalTimeTicker.start();
		}
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
		if (!pausing){
			pausing = true;
			globalTimeTicker.interrupt();
			tAll.interrupt();
			try {
				vlccontroller.pauseAll();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			paused = true;
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
		globalTimeTicker.interrupt();
		vlccontroller.pauseOne(timelineid);
		System.out.println("paused");
		//TODO: Pause the timeline for this display
	}
	
	public HashMap<Integer,TimelineModel> getTimelines() {
		return timelines;
	}
	

	
	/**
	 * Creates a new MediaObject and stores it in the this SessionModule. If a MediaObject with the same path already
	 * exists, the method will not create a new MediaObject, but return the existing one.
	 * @param mst
	 * @param path
	 */
	public String createNewMediaObject(MediaSourceType mst, String path){
		
		// Check if this MediaObject is already stored in the list, by comparing paths
		for (int i=0; i<mediaObjects.size(); i++){
			if (mediaObjects.get(i).getPath().equals(path)) {
				// Return the old MediaObject with equal path
				return "Already exisisted";//ediaObjects.get(i);
			}
		}
		
		// Did not find an old MediaObject with equal path, so create a new one
		
		String name = path.substring(path.lastIndexOf('\\')+1);
		MediaObject mo = new MediaObject(path, name, mst);
		long lenght=vlccontroller.prerunCheck(mo.getPath());
		
		switch(mst){
		case IMAGE: {
			mediaObjects.add(mo);
			mediaObjectsChanged();
			return "mediaObject created";
		}
		case VIDEO: {
			if(lenght>0){
				mo.setLength((int)lenght);
				mediaObjects.add(mo);
				mediaObjectsChanged();
				return "mediaObject created";

			}
			return "MediaObject not created, prerunChecker in VLC failed";
		}
		default: {
			return "MediaObject not created, MediaSourceType not recognized in SessionModule.createNewMediaObject(MediaSourceType mst, String path)";
		}
		}
		

	}

	public ArrayList<MediaObject> getMediaObjects() {
		return this.mediaObjects;
	}
	
	/**
	 * Adds a new TimelineMediaObject to the specified TimelineModel, based on the MediaObject and the startTime.
	 * 
	 * @param mediaObject
	 * @param timeline
	 * @param startTime
	 */
	public String addMediaObjectToTimeline(MediaObject mediaObject, TimelineModel timeline, int startTime){
		TimelineMediaObject tlmo;
		MediaSourceType type = mediaObject.getType(); 
		switch (type){
			case IMAGE: {
				tlmo = new TimelineMediaObject(startTime, IMAGE_DURATION, timeline.getID(), mediaObject);
				break;
			}
			default:
				tlmo = new TimelineMediaObject(startTime, mediaObject.getLength(), timeline.getID(), mediaObject);
				break;
		}
		String result = timeline.addTimelineMediaObject(tlmo);
		timelineChanged(TimeLineChanges.MODIFIED,timeline); //TODO: tell the user what was the outcome of the operation
		return result;
	}
	/**
	 * 
	 * @param tlm
	 * @param tlmo
	 * @param newStart
	 * @param newInternalStart
	 * @param newDuration
	 * @return
	 */
	public String timelineMediaObjectChanged(TimelineModel tlm, TimelineMediaObject tlmo, int newStart, int newInternalStart, int newDuration){
		String result = tlm.timelineMediaObjectChanged(tlmo, newStart, newInternalStart, newDuration);
		timelineChanged(TimeLineChanges.MODIFIED, tlm);
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
	
	/*
	 * Makes sure all the listeners are notified whenever a change to the timelines are done.  
	 */
	private void timelineChanged(TimeLineChanges changeType, TimelineModel timeLineModel) {
		for(SessionListener listener: listeners){
			listener.fireTimelinesChanged(changeType, timeLineModel);
		}
	}
		
	//TODO: we need to specify which mediaobject has been changed. 
	private void mediaObjectsChanged(){
		for(SessionListener listener: listeners){
			listener.fireMediaObjectListChanged();
		}
	}
	/**
	 * removes the timelinemediaobject from the given timelinemodel
	 * @param tlm
	 * @param tlmo
	 */
	public void removeTimelineMediaObjectFromTimeline(TimelineModel tlm, TimelineMediaObject tlmo){
		tlm.removeTimelineMediaObject(tlmo);
		timelineChanged(TimeLineChanges.MODIFIED, tlm);
		
	}
	
	
	public void changeOrderOfTimelines(int timelineID, int newPos ){
		//TODO: Change order of tha timelines
		//This means sending the new order to the GUI,and the GUI must clear all controllers from the timelineContainer. And then reAdd them in the order specified by the timelineOrder
	}

	public ArrayList<Integer> getTimelineOrder() {
		return timelineOrder;
	}
	
	public void globalTimeChanged(){
		for(SessionListener listener:listeners){
			listener.fireGlobalTimeChanged(globaltime);
		}
	}

	public void changeGlobalTime(long newGlobalTime) {
		if(newGlobalTime>=0){
			pauseAll();
			try {
				tAll.join();
				globalTimeTicker.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.globaltime=newGlobalTime;
			globalTimeChanged();
			
			
			
			//stop all mediaPlayers
			for(Integer integer:vlccontroller.getMediaPlayerList().keySet()){
				vlccontroller.stopOne(integer);
			}
			
		}
	}
	
	public ArrayList<Integer> getAvailableDisplays(){
		return new ArrayList<Integer>(displays.keySet());
	}
	
	
	
}
