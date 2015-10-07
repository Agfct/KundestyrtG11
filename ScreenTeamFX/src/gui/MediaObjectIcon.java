/**
 * 
 */
package gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;

/**
 * @author Anders Lunde
 * This is the icon that is being displayed when the media object is being dragged
 */
public class MediaObjectIcon extends GridPane{
	@FXML GridPane grid_pane;

	private MediaObjectType mType = null;
	
	public MediaObjectIcon() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("MediaObjectIcon.fxml")
				);
		
		fxmlLoader.setRoot(this); 
		fxmlLoader.setController(this);
		
		try { 
			fxmlLoader.load();
        
		} catch (IOException exception) {
		    throw new RuntimeException(exception);
		}
	}
	
	@FXML
	private void initialize() {}
	
	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		Point2D localCoords = getParent().sceneToLocal(p);
		
		relocate ( 
				(int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
				(int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
			);
	}
	
	public MediaObjectType getType () { return mType; }
	
	public void setType (MediaObjectType type) {
		
		mType = type;
		
		getStyleClass().clear();
		getStyleClass().add("dragicon");

			if(mType == MediaObjectType.SOUND){
				getStyleClass().add("icon-sound");
			}else{
				getStyleClass().add("icon-video");
			}
			
	}
}
