package gui;
/*
 * All the views that are listening to the session must implement this interface before they can add themselves to the listenersList
 */
public interface SessionListener {
	
	public void fireTimelinesChanged();
	public void fireMediaObjectListChanged();
	
	
	
	

}
