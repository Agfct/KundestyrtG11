/**
 * 
 */
package gui;

import javafx.scene.layout.GridPane;

/**
 * @author Anders
 * This is the visual representation of the MediaObject (Movie, Sound)
 */
public class MediaObjectController extends GridPane{

	
	//Shoud this be a controller ? or shoud it be a Pane ? or other ?
	
	/**
	 * Controller: pros are standard FX pros. 
	 * Cons are that if you drag and drop the media object the controller wont be "moved" 
	 * so you always have to ask controller where the object is.
	 * 
	 * Java has a built in drag and drop, but in order to use this one need to have a proper system of nodes.
	 * is that possible and still get the "snap on seconds" and such?
	 */
}
