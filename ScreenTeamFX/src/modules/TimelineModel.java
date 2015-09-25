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
	
	public int getID() {
		return id;
	}
		
}
