package modules;

import java.util.ArrayList;
import java.util.HashMap;

import vlc.VLCController;

/**
 * 
 * @author BEO
 * Singleton
 * Controls the timelines and their connections to displays. Talks to VLCController and StorageController.
 */
public class TimelineModule {
	private VLCController vlccontroller;
	// Each display can have one or zero timelines
	private HashMap<Integer, TimelineModel> displays;
	private ArrayList<TimelineModel> timelines;
	// Timer for the timeline
	private long globaltime;
	// Queue used when playing timelines
	private ArrayList<Event> performancestack;
	//counter for the id of the timelineModels
	private int tlmID;
	private boolean pausing;
	private Thread t1;
	
	
	public TimelineModule(VLCController vlc) {
		this.timelines = new ArrayList<TimelineModel>();
		this.timelines.add(new TimelineModel(0));
		this.globaltime = 0;
		this.performancestack = new ArrayList<Event>();
		this.tlmID =0;
		this.displays = new HashMap<Integer,TimelineModel>();
		this.vlccontroller =vlc;
		this.pausing = false;
		vlccontroller.createMediaPlayer(tlmID);
		this.t1 = new Thread();
	}

	/**
	 * add a new timeline to the list of timelines
	 * @param tlm
	 */
	public int addTimeline(){
		tlmID +=1;
		TimelineModel tlm = new TimelineModel(tlmID);
		timelines.add(tlm);
		vlccontroller.createMediaPlayer(tlmID);
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
				vlccontroller.deleteMediaPlayer(id);
			}
		}
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
	}
	
	public void assignTimeline(Integer display, TimelineModel tlm){
		//TODO: Check that this is legal. If so: add tlm to display in displays.
		if(displays.isEmpty()){
		}
		else{
			TimelineModel prevtlm = displays.put(display,tlm);
			vlccontroller.setDisplay(tlm.getID(), display);
		}
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
	
	/**
	 * first draft of playing the whole performance. this happens when
	 * the button to play all timelines is pushed.
	 * @param gbltime where the cursor is at when play all is pushed (0 if at start of the timelines)
	 */
	public void playAll(long gbltime){
		//TODO: look over and look for a better way to do this, currently constantly checks if the next event is ready to go or not.
		//ABSOLUTELY NOT DONE NBNBNBNBNBNBNBNB
		if (!pausing){
			this.globaltime = gbltime;
			buildPerformance();
			Thread t = new Thread(){
				public void run(){
					long startp = System.currentTimeMillis();
					long playp = System.currentTimeMillis();
					while (!performancestack.isEmpty()){
						playp = System.currentTimeMillis();
						if (performancestack.get(0).getTime()<= playp-startp){
							ArrayList<Event> temp = new ArrayList<Event>();
							temp.add(performancestack.remove(0));
							while (performancestack.get(0).getTime()==temp.get(0).getTime()){
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
									long spoint = ev2.getTimelineMediaObject().getStartPoint()+ (gbltime-ev2.getTimelineMediaObject().getStart());
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
						if (!performancestack.isEmpty() && performancestack.get(0).getTime()>= 1000+(playp-startp)){
							try {
								System.out.println("sleeping");
								this.sleep(performancestack.get(0).getTime()-(playp-startp)-1000);
								System.out.println("wake wake");
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (pausing){
							try {
								this.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			};
		}
		else{
			notify();
			pausing= false;
		}
		
	}
	
	/**
	 * Goes through all timelines on displays, get all their stacks of events and sort them based on when they 
	 * begin and end. Also check where we are on the globaltime.
	 */
	public void buildPerformance(){
		//Add all Events to list, then sort it
		performancestack = new ArrayList<Event>();
		
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
	/**
	 * first draft of going through the stack and telling the vlccontroller what to do.
	 * This method runs when the user pushes play on one timeline. 
	 * @param display the display should be played
	 * @param glbtime current position of the cursor (0 if at beginning of timeline).
	 */
	public void playOne(Integer display, long glbtime){
		//TODO look over and look for better way to do this, currently constantly checks if the next event is ready to go or not.
		//NOT DONE NBNNBNBNBNBNBNB
		pausing = false;
		this.globaltime = glbtime;
		performancestack.clear();
		ArrayList<Event> tempstack = displays.get(display).getTimelineStack();
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
		t1 = onePlay(glbtime);
		t1.start();
	}
	/**
	 * creates a thread used for playOne
	 * @param glbtime the startpoint of the whole program
	 * @return the thread created
	 */
	private synchronized Thread onePlay(long glbtime){
		Thread t = new Thread(){
			public void run(){
				long startp = System.currentTimeMillis();
				long playp = System.currentTimeMillis();
				while (!performancestack.isEmpty()&& pausing ==false){
					playp = System.currentTimeMillis();
					if (performancestack.get(0).getTime()-glbtime<= playp-startp){
						Event ev2 = performancestack.remove(0);
							if (ev2.getAction()==Action.PLAY){
								vlccontroller.setMedia(ev2.getTimelineid(), ev2.getTimelineMediaObject().getParent().getPath());
								vlccontroller.playOne(ev2.getTimelineid(),ev2.getTimelineMediaObject().getStartPoint());
							}
							else if(ev2.getAction()==Action.STOP){
								vlccontroller.stopOne(ev2.getTimelineid());
							}
							else if(ev2.getAction()==Action.PLAY_WITH_OFFSET){
								vlccontroller.setMedia(ev2.getTimelineid(), ev2.getTimelineMediaObject().getParent().getPath());
								long spoint = ev2.getTimelineMediaObject().getStartPoint()+ (glbtime-ev2.getTimelineMediaObject().getStart());
								vlccontroller.playOne(ev2.getTimelineid(), spoint);
							}
						}
					//thread sleeping if its long until next event
					if (!performancestack.isEmpty() && performancestack.get(0).getTime()-glbtime> 1500+(playp-startp)){
						try {
							System.out.println((performancestack.get(0).getTime()-glbtime)-(playp-startp)-1500);
							this.sleep((performancestack.get(0).getTime()-glbtime)-(playp-startp)-1500);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		};
		
		return t;
	}
	
	/**
	 * pause all the displays and timelines.
	 */
	public void pauseAll(){
		pausing = true;
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
	public void pauseOne(Integer display) throws InterruptedException{
		pausing = true;
		t1.interrupt();
		vlccontroller.pauseOne(displays.get(display).getID());
		//TODO: Pause the timeline for this display
	}
	public ArrayList<TimelineModel> getTimelines() {
		return timelines;
	}
	public void setTimelines(ArrayList<TimelineModel> timelines) {
		this.timelines = timelines;
	}
}
