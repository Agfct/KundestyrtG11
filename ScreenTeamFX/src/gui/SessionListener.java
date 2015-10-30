package gui;

import modules.TimeLineChanges;
import modules.TimelineModel;

/*
 * All the views that are listening to the session must implement this interface before they can add themselves to the listenersList
 */
public interface SessionListener {
	
	public void fireTimelinesChanged(TimeLineChanges changeType, TimelineModel timeLineModel);
	public void fireMediaObjectListChanged();
	public void fireGlobalTimeChanged(long newGlobalTime);
	public void fireSessionLenghtChanged();
	
	
	
	

}
