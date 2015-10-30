package modules;

import java.io.Serializable;

/*
 * 
 * @author ole-s
 * Enum for the actions that should be performed when handling events.
 * 
 * STOP: Stop the currently playing MediaObject
 * PLAY: Start playing the MediaObject from the start of it (defined in the MediaObject.startVideo, for videos)
 * PLAY_WITH_OFFSET: Start playing the video somewhere after the start. This happens because the globaltimer is placed
 * 						in the video when the user presses play.	
 *
 */

public enum Action implements Serializable{
	STOP, PLAY, PLAY_WITH_OFFSET, PAUSE, SHOW, HIDE
}
