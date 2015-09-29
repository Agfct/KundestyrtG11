package modules;

import java.util.ArrayList;
import java.util.Collections;

public class TimelineModel {
	
	private ArrayList<MediaObject> mediaObjects;
	private final int id;
	private ArrayList<Event> timelineStack;
	public TimelineModel(ArrayList<MediaObject> mediaObjects, int id) {
		super();
		this.mediaObjects = mediaObjects;
		this.id = id;
		this.timelineStack = new ArrayList<Event>();
	}

	public ArrayList<MediaObject> getMediaObjects() {
		return mediaObjects;
	}

	public void setMediaObjects(ArrayList<MediaObject> mediaObjects) {
		this.mediaObjects = mediaObjects;
	}

	public int getID() {
		return id;
	}
	
	public void addMediaObject(MediaObject m){
		mediaObjects.add(m);
		timelinechanged();
	}
	
	public void removeMediaObject(MediaObject m){
		mediaObjects.remove(m);
		timelinechanged();
	}
	public void timelinechanged(){
		timelineStack.clear();
		MediaObject mO;
		Event start;
		Event end;
		for(int i=0;i>=mediaObjects.size();i++){
			mO = mediaObjects.get(i);
			if (mO instanceof MediaObjectVideo){
				start = new Event(((MediaObjectVideo) mO).getStartTime(), id, Action.PLAY);
				end = new Event(((MediaObjectVideo) mO).getEndVideo(), id, Action.STOP);
				timelineStack.add(start);
				timelineStack.add(end);
			}
		Collections.sort(timelineStack);
		}
	}
		
}
