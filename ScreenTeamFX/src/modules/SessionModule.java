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
 * @author Baptiste Masselin, Eirik Z. Wold, Ole S.L. Skrede, Magnus Gundersen, Anders Lunde.
 * Controls the timelines and their connections to displays. Talks to VLCController and StorageController.
 */
public class SessionModule implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3548566162984308320L;
    private VLCController vlccontroller;
    private WindowDisplay windowdisplay;
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
    private boolean inter;
    private boolean tAllCalledPause;
    private Thread t1;
    private Thread tAll;

    private long sessionLength;

    private Thread globalTimeTicker;

    private ArrayList<SessionListener> listeners;
    private ArrayList<Integer> timelineOrder;
    private ArrayList<String> shownwindows;
    private ArrayList<Event> lastEvents;
    private ArrayList<Event> timelinebarStopEvents;
    // Used when saving and loading, to check if the loaded session has the same number of displays as the loaded one
    private int numberOfAvailableDisplays;

    // Constant used when creating TimelineMediaObjects that are images or windows. Used as a reasonable duration when first appearing on a timeline.
    private final long IMAGE_DURATION = 30000;
    private final long WINDOW_DURATION = 30000;
    
    //configuration for the vlc. 
    String[] vlcConfiguration = {};


    public SessionModule(VLCController vlc, WindowDisplay wdi) {
        this.timelines = new HashMap<Integer,TimelineModel>();
//		this.timelines.put(0,new TimelineModel(0));
        this.mediaObjects = new ArrayList<MediaObject>();
        this.globaltime = 0;
        this.performancestack = new ArrayList<Event>();
        this.tlmID =0;
        this.sessionLength = 2000; //Seconds not ms
        this.displays = new HashMap<Integer,TimelineModel>();
        this.listeners = new ArrayList<SessionListener>();
        this.vlccontroller = vlc;
        this.windowdisplay = wdi;
        this.pausing = true;
        this.paused = true;
        this.inter = false;
        this.tAllCalledPause = false;
//		vlccontroller.createMediaPlayer(tlmID);
        this.t1 = new Thread();
        this.tAll = new Thread();
        this.globalTimeTicker = new Thread();
        this.timelineOrder=new ArrayList<Integer>();
        this.shownwindows = new ArrayList<String>();
        this.numberOfAvailableDisplays = -1;
        this.timelinebarStopEvents = new ArrayList<Event>();
    }

    /**
     * add a new timeline to the list of timelines
     * @param tlm
     */
    public int addTimeline(){
        tlmID +=1;
        TimelineModel tlm = new TimelineModel(tlmID);
        timelines.put(tlmID,tlm);
        vlccontroller.createMediaPlayer(tlmID, vlcConfiguration);
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

    public void removeAllTimlines(){
        // Create copy of the IDs that shall be remove (all)
        ArrayList<Integer> timelineIDs = new ArrayList<Integer>();
        for(int i=0; i<timelineOrder.size(); i++){
            timelineIDs.add(new Integer(timelineOrder.get(i)));
        }

        // Remove the timlelines
        for(int i=0; i<timelineIDs.size(); i++){
            removeTimeline(timelineIDs.get(i));
        }
    }

    /**
     * goes through all displays and removes tlm if it is assigned to said display
     * @param tlm
     */
    public void unassignTimeline(TimelineModel tlm){
        if(displays.isEmpty()){
        }
        else{
            //Check every displays
            for(Integer i : displays.keySet()){
                if(displays.get(i)==tlm){
                    displays.put(i,null);
                    vlccontroller.unassignDisplay(tlm.getID());
                    tlm.removeDisplay(i);
                    System.out.println(i);
                    timelineChanged(TimeLineChanges.MODIFIED, tlm);
                }
            }
        }
    }

    /**
     * Assigns a timeline to be played on a display
     * @param display
     * @param tlm the timeline that is to be assigned to the display
     */
    public void assignTimeline(Integer display, TimelineModel tlm){
        if(!displays.containsKey(display)){
        }
        else{
        	tlm.removeDisplay(display);
            unassignTimeline(tlm);
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
        numberOfAvailableDisplays = displays.size();
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

    public int getNumberOfAbailableDisplays(){
        return numberOfAvailableDisplays;
    }
    
    /**
     * calls a method in vlccontroller that recreates all media players with new settings specified in options.
     * @param options the new set of options
     */
    public void updateMediaPlayers(){
    	vlccontroller.updateOptions(vlcConfiguration);
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
            //waiting for the threads to finish if paused earlier
            try {
            	System.out.println("nononononono");
                tAll.join();
                System.out.println("yeyeyyeyyyeye");
                globalTimeTicker.join();
            } catch (InterruptedException e) {
                System.out.println("interrupted waiting for tAll and/or the globalTimeTicker to die");
            }
            //rebuilds the performance in case of changes or new startpoint/globaltime
            buildPerformance();
            System.out.println("built");
            //creates the thread for excecuting the performance
            tAll = allPlay(globaltime);
            System.out.println("created");
            //creates the thread for increasing the globaltime
            globalTimeTicker=tickGlobalTime(globaltime);
            pausing = false;
            tAllCalledPause = false;
            //starts the threads
            tAll.start();
            System.out.println("started");
//            globalTimeTicker.start();
        }
    }

    /**
     * creates a thread That every second updates the globaltime
     * @param globalTimeAtStart
     * @return the thread
     */
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
                        this.sleep(100);
                    } catch (InterruptedException e) {
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
     * @return the thread created, to be started elsewhere
     */
    private synchronized Thread allPlay(long glbtime){
        //creates a thread
        Thread tAll1 = new Thread(){
            public void run(){
                System.out.println("RUN ALLPLAY");
                System.out.println(performancestack.size());
                //set startp and playp to get a view on real time seconds
                long startp = System.currentTimeMillis();
                long playp = System.currentTimeMillis();
                //a map for seeking multiple videos with the seekmultiple method further down
                Map<Integer,Long> pplay = new HashMap<Integer,Long>();
                //a check if a part of the thread is interrupted
                inter =false;
                //if there are no more tasks to be done or the program has been paused, then the while loop ends
                outerloop:
                while (!performancestack.isEmpty() && pausing == false){
                	//update playp to current time to gage the time since start
                    playp = System.currentTimeMillis();
                    //checks if next event should happen yet continues if yes
                    if (performancestack.get(0).getTime()-glbtime<= playp-startp){
                        ArrayList<Event> temp = new ArrayList<Event>();
                        temp.add(performancestack.remove(0));
                        pplay.clear();
                        //find all events that should happen at the same time
                        while (!performancestack.isEmpty() && performancestack.get(0).getTime()-glbtime<=playp-startp){
                            temp.add(performancestack.remove(0));
                        }
                        //go through all events that should happen and checks what should be done through the action of the event
                        for (Event ev2 : temp){
                        	//if its a PLAY event then a video should be played from the beginning, so we set the video to the mediaplayer corresponding to the timeline and then play it
                            if (ev2.getAction()==Action.PLAY){
                                vlccontroller.setMedia(ev2.getTimelineid(), ev2.getTimelineMediaObject().getParent().getPath());
                                pplay.put(ev2.getTimelineid(), ev2.getTimelineMediaObject().getStartPoint());

                            }
                            //if its a STOP event then we stop the video on the mediaplayer corresponding to the timeline
                            else if(ev2.getAction()==Action.STOP){
                                vlccontroller.stopOne(ev2.getTimelineid());
                            }
                            //if its a PLAY_WITH_OFFSET event then the video should start somewhere out into the video so we need to seek before playing
                            //this happens if you dont start at the beginning of the playsession (globaltime is not 0)
                            else if(ev2.getAction()==Action.PLAY_WITH_OFFSET){
                                if (vlccontroller.getMediaPlayerList().get(ev2.getTimelineid()).getMediaPath()!=ev2.getTimelineMediaObject().getParent().getPath()){
                                    vlccontroller.setMedia(ev2.getTimelineid(), ev2.getTimelineMediaObject().getParent().getPath());
                                }
                                //calculate where in the video to start
                                long spoint = ev2.getTimelineMediaObject().getStartPoint()+ (glbtime-ev2.getTimelineMediaObject().getStart());
                                pplay.put(ev2.getTimelineid(), spoint);
                            }
                            //if its a SHOW event then a window should be shown on the display with the timeline assigned to it.
                            else if(ev2.getAction()==Action.SHOW){
                            	//go through all displays
                                for(Integer dis:displays.keySet()){
                                	if(displays.get(dis)==null){
                                		continue;
                        
                                	}
                                	//if dis has the timeline assigned to it
                                    if (displays.get(dis).getID()==ev2.getTimelineid()){
                                    	//hide the jframe for the vlc and maximize the window on the display
                                        vlccontroller.showmp(ev2.getTimelineid(), false);
                                        windowdisplay.WindowManipulation(ev2.getTimelineMediaObject().getParent().getPath(), false, dis);
                                        vlccontroller.maximize(ev2.getTimelineid());
                                        shownwindows.add(ev2.getTimelineMediaObject().getParent().getPath());
                                        break;
                                    }
                                }
                            }
                            //if its a HIDE event then we try to hide the window/minimize the window
                            else if(ev2.getAction()==Action.HIDE){
                            	//show the jframe for for the vlc corresponding to the timeline and minimize the window
                                vlccontroller.showmp(ev2.getTimelineid(), true);
                                windowdisplay.WindowManipulation(ev2.getTimelineMediaObject().getParent().getPath(), true, 0);
                                vlccontroller.maximize(ev2.getTimelineid());
                                shownwindows.remove(ev2.getTimelineMediaObject().getParent().getPath());
                            }
                            else if(ev2.getAction()==Action.PAUSE_ALL){
                            	tAllCalledPause = true;
                            	pauseAll();
                            	break outerloop;
//                            	MainModuleController.getInstance().getSession().pauseAll();
                            }
                        }
                        try {
                        	//seek in all the videos then play all
                        	if (pplay.size()>0){
                        		vlccontroller.SeekMultiple(pplay);
                        		vlccontroller.playAll();
                        	}
                            //start the globaltimeticker if it has not started yet
                            if (!globalTimeTicker.isAlive() && pausing == false){
                                startp = System.currentTimeMillis();
                                globalTimeTicker.start();
                            }
                        } catch (InterruptedException e) {
                            System.out.println("interrupted seekmultiple or playAll while playing");
                            inter = true;
                            
                        }
                    }
                    //thread sleeping if its long until next event
                    if (!performancestack.isEmpty() && performancestack.get(0).getTime()-glbtime> 1500+(playp-startp) && !inter){
                        try {                        	
                            this.sleep((performancestack.get(0).getTime()-glbtime)-(playp-startp)-1500);
                        } catch (InterruptedException e) {
                        	inter = true;
                        }
                    }
                }
                //failsafe to allow the globaltimelineticker to start if there are no timelinemediaobject assigned/the performancestack is empty
                if (!globalTimeTicker.isAlive() && pausing == false){
                    startp = System.currentTimeMillis();
                    globalTimeTicker.start();
                }
            }
        };
        //returns the thread so it can be started in playAll function
        return tAll1;
    }
    
    public ArrayList<Event> getBreakpoints(){
    	return timelinebarStopEvents;
    }
    
    public void addBreakpoint(long time){
    	Event newStop = new Event(time, 0, Action.PAUSE_ALL, null);
    	if(insertEventInTimelinebarStopEvents(newStop)){
    		timelinebarChanged();
    	}
    }
    
    private boolean insertEventInTimelinebarStopEvents(Event e){
    	long thisETime = e.getTime();
    	for(int i=0; i<timelinebarStopEvents.size(); i++){
    		long otherETime = timelinebarStopEvents.get(i).getTime();
    		if( thisETime==otherETime ){
    			return false;
    		}
    		if( thisETime<otherETime ){
    			timelinebarStopEvents.add(i, e);
    			return true;
    		}
    	}
    	timelinebarStopEvents.add(e);
    	return true;
    }
    
    public void removeBreakpoint(long time){
    	for(int i=0; i<timelinebarStopEvents.size(); i++){
    		if( timelinebarStopEvents.get(i).getTime() == time ){
    			timelinebarStopEvents.remove(i);
    			timelinebarChanged();
    			return;
    		}
    	}
    }


    /**
     * Goes through all timelines assigned to a display, get all their stacks of events and sort them based on when they
     * begin and end. Also check where we are on the globaltime and set videos to play with offset if the already should have begun but not yet ended.
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
                	//creates a new Event so that in case we change the action to PLAY_WITH_OFFSET then we don't change the event in the timelinemodels.
                    Event ev = new Event(ev2.getTime(), ev2.getTimelineid(), ev2.getAction(), ev2.getTimelineMediaObject());
                    if(ev.getTimelineMediaObject().getParent().getType()==MediaSourceType.VIDEO){
                        if(ev.getAction() == Action.PLAY){
                        	//if its play, check when it should be started and add the event if it is after current time
                            if(ev.getTime()>=globaltime){
                                performancestack.add(ev);
                            }
                            //change to PLAY_WITH_OFFSET if the globaltime is between the start and end of the timelinemediaobject
                            else if(ev.getTime()<globaltime && ev.getTimelineMediaObject().getEnd()>globaltime){
                                ev.setAction(Action.PLAY_WITH_OFFSET);
                                performancestack.add(ev);
                            }
                        }
                        else if(ev.getAction()==Action.STOP){
                        	//if stop happens after current time then add it
                            if (ev.getTime()>=globaltime){
                                performancestack.add(ev);
                            }
                        }
                    }
                    else if(ev.getTimelineMediaObject().getParent().getType()==MediaSourceType.AUDIO){
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
                    //Does the same as with objects that are VIDEO only that they are IMAGEs
                    else if(ev.getTimelineMediaObject().getParent().getType()==MediaSourceType.IMAGE){
                        if(ev.getAction() == Action.PLAY){
                            if(ev.getTime()>=globaltime){
                                performancestack.add(ev);
                            }
                            else if(ev.getTime()<globaltime && ev.getTimelineMediaObject().getEnd()>globaltime){
                                ev.setAction(Action.PLAY);
                                performancestack.add(ev);
                            }
                        }
                        else if(ev.getAction()==Action.STOP){
                            if (ev.getTime()>=globaltime){
                                performancestack.add(ev);
                            }
                        }
                    }
                    //if the type is a WINDOW then the possible actions are SHOW and HIDE
                    else if(ev.getTimelineMediaObject().getParent().getType()==MediaSourceType.WINDOW){
                    	//if action is SHOW and it should be shown after or during current time then add
                        if (ev.getAction()==Action.SHOW){
                            if(ev.getTime()>=globaltime){
                                performancestack.add(ev);
                            }
                            else if(ev.getTime()<globaltime && ev.getTimelineMediaObject().getEnd()>globaltime){
                                performancestack.add(ev);
                            }
                        }
                        else if(ev.getAction()==Action.HIDE){
                        	//add event to hide the window if it should happen after current time
                            if(ev.getTime()>=globaltime){
                                performancestack.add(ev);
                            }
                        }
                    }
                }
            }
        }
        
        for(int i=0; i<timelinebarStopEvents.size(); i++){
        	if( (globaltime+7) < timelinebarStopEvents.get(i).getTime() ){
        		performancestack.add(timelinebarStopEvents.get(i));
        	}
        }
        //sort the stack in case something got wierd. sorted by time the event happens in increasing order.
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
    	//nothing should happen if already pausing
        if (!pausing){
        	//set pausing to true so that the threads will end.
            pausing = true;
            //wake the threads if they sleep so they can end
            inter = true;
            globalTimeTicker.interrupt();
            if(!tAllCalledPause){
            	tAll.interrupt();
            	tAllCalledPause = false;
            }
            try {
            	//call the pauseAll function to pause the videos that are playing
                vlccontroller.pauseAll();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            paused = true;
        }        
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

    /**
     * @return a hashmap with the id of a timeline as key and the timelinemodel as value
     */
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
    	System.out.println("[SESSION module]" + mst);
        // Check if this MediaObject is already stored in the list, by comparing paths
        for (int i=0; i<mediaObjects.size(); i++){
            if (mediaObjects.get(i).getPath().equals(path)) {
                // Return the old MediaObject with equal path
                return "Already exisisted";//ediaObjects.get(i);
            }
        }

        // Did not find an old MediaObject with equal path, so create a new one
        String name = "";
        MediaObject mo = new MediaObject(path, name, mst);

        switch(mst){
            case IMAGE: {
                name = path.substring(path.lastIndexOf('\\')+1);
                mo.setName(name);
                break;
            }
            case AUDIO: {
                name = path.substring(path.lastIndexOf('\\')+1);
                mo.setName(name);
                long lenght=vlccontroller.prerunCheck(mo.getPath());
                if(lenght>0){
                    mo.setLength((int)lenght);
                }
                else{
                    return "MediaObject not created, prerunChecker in VLC failed";
                }
                break;
            }
            case WINDOW: {
                name = path;
                mo.setName(name);
                break;
            }
            case VIDEO: {
                name = path.substring(path.lastIndexOf('\\')+1);
                mo.setName(name);
                long lenght=vlccontroller.prerunCheck(mo.getPath());
                if(lenght>0){
                    mo.setLength((int)lenght);
                }
                else{
                    return "MediaObject not created, prerunChecker in VLC failed";
                }
                break;
                
            }
            default: {
                break;
            }
        }

        mediaObjects.add(mo);
        mediaObjectsChanged();
        return "mediaObject created";
    }

    /**
     * Removes all TimelineMediaObjects with the parameter MediaObject as parent, and removes
     * the parameter MediaObject from this SessionModule
     * @param mo
     */
    public void removeMediaObject(MediaObject mo){
    	// For all timelines
    	for(Integer i : timelines.keySet()){
    		TimelineModel timeline = timelines.get(i);
    		ArrayList<TimelineMediaObject> tlmoToRemove = new ArrayList<TimelineMediaObject>();
    		// Find all the TimelineMediaObects that we want to remove. Can't remove from the list we are iterating through
    		for(TimelineMediaObject tlmo : timeline.getTimelineMediaObjects()){
    			if( tlmo.getParent().getPath().equals(mo.getPath()) ){
    				tlmoToRemove.add(tlmo);
    			}
    		}
    		// Remove them
    		for(TimelineMediaObject tlmo : tlmoToRemove){
    			this.removeTimelineMediaObjectFromTimeline(timeline, tlmo);
    		}
    	}
    	
    	// Finally remove the mediaObject
    	mediaObjects.remove(mo);
    	this.mediaObjectsChanged();
    }
    
    /**
     * Changes the path of the parameter MediaObject to the parameter String newPath. Checks that the new path points to a 
     * MediaObject of the same type, and if it is a VIDEO or AUDIO then also check the length. If anything is different, the change 
     * is not performed.
     * @param mo
     * @param newPath
     * @return
     */
    public boolean changeMediaObject(MediaObject mo, String newPath){
    	if(mo.getType()==MediaSourceType.WINDOW){
    		mo.setPath(newPath);
    		mo.setName(newPath);
    		mo.setValidPath(true);
    	}
    	else{
    		MediaSourceType newMST = FileController.getMediaSourceType(newPath);
    		if(newMST==null){
    			return false;
    		}
    		if(newMST != mo.getType()){
    			return false;
    		}
    		
    		if(newMST==MediaSourceType.AUDIO || newMST==MediaSourceType.VIDEO){
    			long length = vlccontroller.prerunCheck(newPath);
    			if( length != mo.getLength() ){
    				return false;
    			}
    		}
    		// Passed all the tests, so we can set the new path.
    		mo.setPath(newPath);
    		String name = FileController.getTitle(newPath);
    		mo.setName(name);
    		mo.setValidPath(true);
    	}
    	
    	
    	MainGUIController.getInstance().updateMediaObjects();
    	return true;
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
            case WINDOW: {
                tlmo = new TimelineMediaObject(startTime, WINDOW_DURATION, timeline.getID(), mediaObject);
                // Check that it does not collide with anything
                if( checkWindowCollision(tlmo) ){
                    return "Window ("+mediaObject.getName()+") collides with another window of the same type. It was not added.";
                }
                break;
            }
            default:
                tlmo = new TimelineMediaObject(startTime, mediaObject.getLength(), timeline.getID(), mediaObject);
                break;
        }
        String result = timeline.addTimelineMediaObject(tlmo);
        timelineChanged(TimeLineChanges.MODIFIED,timeline); //TODO: tell the user what was the outcome of the operation
        checkSessionSize(tlmo.getStart(), tlmo.getDuration());
        return result;
    }

    /**
     * Go through all timelines and check if the timlinemediaobject (which should be a WINDOW) collides with
     * another timlinemediaobject for the same window.
     * Returns true if there is a collision
     * @param tlmo
     * @return
     */
    public boolean checkWindowCollision(TimelineMediaObject tlmo) {
        if(tlmo.getParent().getType() != MediaSourceType.WINDOW){
            return false;
        }

        for(Integer i : timelines.keySet()){
            TimelineModel tlm = timelines.get(i);
            for(TimelineMediaObject tlmoToCompare : tlm.getTimelineMediaObjects()){
                if(tlmoToCompare.getParent().getType() != MediaSourceType.WINDOW){
                    continue;
                }
                if( !tlmoToCompare.getParent().getPath().equals(tlmo.getParent().getPath()) ){
                    continue;
                }
                // From here on, both timelinemediaobjects are of type WINDOW and they have the same MediaObject parent
                long sp = tlmo.getStart();
                long ep = sp + tlmo.getDuration();
                long osp = tlmoToCompare.getStart();
                long oep = osp + tlmoToCompare.getDuration();

                if( osp <= sp && sp <= oep || osp <= ep && ep <= oep ){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Duplicates the content from TimlineModel tlm (except from window management) into the TimlineModel with ID == timelineInt.
     * Returns true if this is done successfully, and false if there is some problem.
     * @param tlm
     * @param timelineInt
     */
    public boolean duplicateToTimeline(TimelineModel tlm, int timelineInt) {
        // Get the list of TimelineMediaObjects that shall be duplicated, and the timeline (duplicate) they shall be duplicated to
        ArrayList<TimelineMediaObject> timelineMediaObjects = tlm.getTimelineMediaObjects();
        TimelineModel duplicate = timelines.get(timelineInt);
        if(duplicate == null){
            System.out.println("[Session;odule.duplicateToTimeline()] Could not find TimelineModel with id " + timelineInt);
            return false;
        }

        // Go through the TimelineMediaObjects, add those that are not MediaSourceType.WINDOW, and update the timeline
        for(TimelineMediaObject tlmToCopy : timelineMediaObjects) {
            MediaObject parent = tlmToCopy.getParent();
            MediaSourceType mediaSourceType = parent.getType();
            if( mediaSourceType == MediaSourceType.WINDOW ){
                // If the timelineMediaObject is a WINDOW operation, then skip to the next timelineMediaObject
                continue;
            }

            long startTime = tlmToCopy.getStart();
            long startPoint = tlmToCopy.getStartPoint();
            long duration = tlmToCopy.getDuration();

            TimelineMediaObject tlmoToAdd = new TimelineMediaObject(startTime, duration, timelineInt, parent);
            tlmoToAdd.setStartPoint(startPoint);
            duplicate.addTimelineMediaObject(tlmoToAdd);
            timelineChanged(TimeLineChanges.MODIFIED, duplicate);
            checkSessionSize(tlmoToAdd.getStart(), tlmoToAdd.getDuration());
        }
        return true;
    }

    /**
     * When the user wants to increase the length of a session we increase the sessionLength and updates the GUI.
     * @param start
     * @param duration
     */
    private void checkSessionSize(long start, long duration){
        if(start+duration > sessionLength*1000){
            sessionLength = sessionLength*2; //Doubles the size of the session
            sessionLenghtChanged();
            checkSessionSize(start, duration);
        }
       
    }

    private void sessionLenghtChanged(){
        for(SessionListener listener:listeners){
            listener.fireSessionLenghtChanged();
        }
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
        checkSessionSize(tlmo.getStart(), tlmo.getDuration());
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
        if(listeners!=null){
            for(SessionListener listener: listeners){
                listener.fireTimelinesChanged(changeType, timeLineModel);
            }
        }
    }
    
    private void timelinebarChanged(){
    	if(listeners!=null){
    		for(SessionListener listener : listeners){
    			listener.fireTimelinebarChanged();
    		}
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

    public long getSessionLength(){
        return sessionLength;
    }

    public long getGlobalTime(){
        return globaltime;
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
                System.out.println("interrupted join on tALL or globalTimeTicker when changing globaltime");
            }

            this.globaltime=newGlobalTime;
            globalTimeChanged();


            for(String wind:shownwindows){
            	windowdisplay.WindowManipulation(wind, true, 0);
            }
            shownwindows.clear();

            //stop all mediaPlayers
            for(Integer integer:vlccontroller.getMediaPlayerList().keySet()){
                vlccontroller.stopOne(integer);
                if (vlccontroller.getMediaPlayerList().get(integer).getDisplay()!=-1){
                	vlccontroller.showmp(integer, true);
                	vlccontroller.maximize(integer);
                }
            }
        }
    }

    public ArrayList<Integer> getAvailableDisplays(){
        return new ArrayList<Integer>(displays.keySet());
    }

    public void removeVLCController() {
        vlccontroller = null;
    }

    public void removeThreads() {
        t1 = null;
        tAll = null;
        globalTimeTicker = null;
    }

    public void removeAssignedDisplays(){
        for (Integer i: displays.keySet()) {
            displays.put(i, null);
        }
    }

    public void reinitialize(VLCController vlc, WindowDisplay wd) {
        this.displays = new HashMap<Integer,TimelineModel>();
        this.listeners = new ArrayList<SessionListener>();
        this.vlccontroller = vlc;
        this.windowdisplay = wd;
        this.t1 = new Thread();
        this.tAll = new Thread();
        this.globalTimeTicker = new Thread();

        for(Integer i : timelines.keySet()){
            vlccontroller.createMediaPlayer(i, vlcConfiguration);
        }

    }

    public ArrayList<SessionListener> removeListeners() {
        ArrayList<SessionListener> out = listeners;
        listeners = null;
        return out;
    }

    public VLCController removeAndGetVLCController() {
        VLCController out = vlccontroller;
        vlccontroller = null;
        return out;
    }

    public Thread removeT1() {
        Thread out = t1;
        t1 = null;
        return out;
    }

    public Thread removeTAll() {
        Thread out = tAll;
        tAll = null;
        return out;
    }

    public Thread removeGlobalTimeTicker() {
        Thread out = globalTimeTicker;
        globalTimeTicker = null;
        return out;
    }

    public HashMap<Integer, TimelineModel> removeDisplays() {
        HashMap<Integer, TimelineModel> out = displays;
        displays = null;
        return out;
    }
    
    public WindowDisplay removeWindowDisplay(){
    	WindowDisplay out = windowdisplay;
    	windowdisplay = null;
    	return out;
    }

    public void setListeners(ArrayList<SessionListener> l) {
        listeners = l;
    }

    public void setVLCController(VLCController vlcc) {
        vlccontroller = vlcc;
    }

    public void setT1(Thread t) {
        t1 = t;
    }

    public void setTAll(Thread t) {
        tAll = t;
    }

    public void setGlobalTimeTicker(Thread gtt) {
        globalTimeTicker = gtt;
    }

    public void setDisplays(HashMap<Integer, TimelineModel> disp) {
        displays = disp;
    }
    
    public void setWindowDisplay(WindowDisplay wd){
    	windowdisplay = wd;
    }


    /**
     * Removes all assigned display from all the timelines. Used only when loading, before updating the GUI.
     */
    public void removeAllTimlineDisplayAssignments() {
        for(Integer i : timelines.keySet()){
            timelines.get(i).removeAllDisplays();
        }
    }
    /**
     * Gets the available windows from the windowDisplay. Then extracts the titles of the windows, and sends it to whoever asks
     * @return
     */
    public ArrayList<String> getAvailableWindows() {
        windowdisplay.getAllWindows(); //updates the windowsList
        ArrayList<WindowInfos> windowInfos = windowdisplay.getWindowInfoList();
        System.out.println(windowInfos);

        ArrayList<String> windowsListNames = new ArrayList<String>();
        for(WindowInfos windowInfo:windowInfos){
            windowsListNames.add(windowInfo.getTitle()); // title is the name of the window as shown at the top of the window
        }
        return windowsListNames;
    }
    
    /**
     * This methods rearranges the order of the timelines
     * @param direction
     * @param timelineModel
     */
	public void moveTimeline(String direction, TimelineModel timelineModel) {
		//Finds the given model in the order-arrayList:
		for(int i =0; i<timelineOrder.size();i++){
			if(timelineOrder.get(i)==timelineModel.getID()){//Found the corresponding timeline
				if(direction.equals("up")){

					if(i!=0){//If the timeline is not on the top of the stack:
						Integer timelineToBeRearranged=timelineOrder.remove(i);
						timelineOrder.add(i-1,timelineToBeRearranged); //puts it in the position above
						timelineChanged(TimeLineChanges.ORDER, timelineModel);
					}
				}
				else if(direction.equals("down")){//If the timeline is not at the bottom of the stack:
					if(i!=timelineOrder.size()-1){
						Integer timelineToBeRearranged=timelineOrder.remove(i);
						timelineOrder.add(i+1,timelineToBeRearranged);//puts it in the position below
						timelineChanged(TimeLineChanges.ORDER, timelineModel);
					}
				}
				break;
			}
		}
	}
	
	public String[] getVLCConfiguration(){
		return vlcConfiguration;
	}
	
	public void setVLCConfiguration(String[] newConfig){
		vlcConfiguration=newConfig;
		updateMediaPlayers();
	}
	
	
	
	/*
	 * Baptiste: Get creation for the test
	 */

    
   public boolean getPausing(){
	   return pausing;
   }
   public boolean getPaused(){
	   return paused;
   }

   public WindowDisplay getWindowDisplay(){
    	return windowdisplay;
    }
    public int gettlmID(){
    	return tlmID;
    }
    public HashMap<Integer,TimelineModel> getdisplays(){
    	return displays;
    }

    public VLCController getvlccontroller(){
    	return vlccontroller;
    }
    public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public long getGlobaltime() {
		return globaltime;
	}
	public ArrayList<Event> getPerformancestack() {
		return performancestack;
	}
	public boolean isPausing() {
		return pausing;
	}
	public boolean isPaused() {
		return paused;
	}
	public Thread getT1() {
		return t1;
	}
	public Thread gettAll() {
		return tAll;
	}
	public Thread getGlobalTimeTicker() {
		return globalTimeTicker;
	}
	public ArrayList<SessionListener> getListeners() {
		return listeners;
	}
	public ArrayList<String> getShownwindows() {
		return shownwindows;
	}
	public ArrayList<Event> getLastEvents() {
		return lastEvents;
	}
	public int getNumberOfAvailableDisplays() {
		return numberOfAvailableDisplays;
	}
	public long getIMAGE_DURATION() {
		return IMAGE_DURATION;
	}

	



}
