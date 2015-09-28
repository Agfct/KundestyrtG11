package modules;

import java.util.ArrayList;

public class TimelineModel {
	
	private ArrayList<MediaObject> mediaObjects;
	private final int id;
	public TimelineModel(ArrayList<MediaObject> mediaObjects, int id) {
		super();
		this.mediaObjects = mediaObjects;
		this.id = id;
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
		
}
