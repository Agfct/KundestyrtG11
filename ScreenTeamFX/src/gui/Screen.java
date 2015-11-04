/**
 * 
 */
package gui;

import javafx.scene.Scene;

/**
 * @author Anders Lunde
 *
 *All GUI screen classes inherits from the Screen interface, 
 *making the methods for the MainGUIController the same for every screen.
 */
public interface Screen {
	
	/**
	 * Returns the scene controlled by the screen class.
	 * @return
	 */
	public Scene getScene();
	

}
