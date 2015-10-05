/**
 * 
 */
package gui;

import javafx.event.EventHandler;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;

/**
 * @author Anders
 * This is the visual representation of the MediaObject (Movie, Sound)
 */
public class MediaObjectController extends GridPane{


	public MediaObjectController(){
		setStyle("-fx-background-color: BLUE");
		setMinHeight(75);
		setMinWidth(100);
		
		//TODO: This
		setOnDragDetected(new EventHandler<MouseEvent>() {
		    public void handle(MouseEvent event) {
		    	System.out.println("Dragged");
		        /* drag was detected, start a drag-and-drop gesture*/
		        /* allow any transfer mode */
		        Dragboard db = startDragAndDrop(TransferMode.MOVE);
		        
		        /* Put a string on a dragboard */
//		        ClipboardContent content = new ClipboardContent();
//		        content.putString(getText());
//		        db.setContent(content);
//		        
		        event.consume();
		    }
		});
	}
	
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
