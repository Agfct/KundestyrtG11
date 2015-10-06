/**
 * 
 */
package gui;

import java.io.IOException;
import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * @author Anders
 * This is the visual representation of the MediaObject (Movie, Sound)
 */
public class MediaObjectController extends GridPane{

	private FXMLLoader fxmlLoader;
	private AdvancedScreenController parentController;

	//Variables used for dragging/dropping
	private EventHandler <DragEvent> mContextDragOver;
	private EventHandler <DragEvent> mContextDragDropped;
	private Point2D mDragOffset = new Point2D (0.0, 0.0);
	private MediaObjectType mType = null;
	
	public MediaObjectController(){
		setStyle("-fx-background-color: BLUE");
		
		
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("MediaObject.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.setRoot(this);
			fxmlLoader.load();
		} catch (IOException e) {
			System.out.println("Failed to load TimelineController FXML");
			e.printStackTrace();
		}
		
//		//TODO: This
//		setOnDragDetected(new EventHandler<MouseEvent>() {
//		    public void handle(MouseEvent event) {
//		    	System.out.println("Dragged");
//		        /* drag was detected, start a drag-and-drop gesture*/
//		        /* allow any transfer mode */
//		        Dragboard db = startDragAndDrop(TransferMode.MOVE);
//		        
//		        /* Put a string on a dragboard */
////		        ClipboardContent content = new ClipboardContent();
////		        content.putString(getText());
////		        db.setContent(content);
////		        
//		        event.consume();
//		    }
//		});
	}
	
	/**
	 * This method is ran when the FXML is initialized
	 */
	@FXML
	private void initialize() {
		System.out.println("init MediaHandlers");
		buildNodeDragHandlers();
	}
	
	public MediaObjectType getType () { return mType; }
	
	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		Point2D localCoords = getParent().sceneToLocal(p);
		
		relocate ( 
				(int) (localCoords.getX() - mDragOffset.getX()),
				(int) (localCoords.getY() - mDragOffset.getY())
			);
	}
	/**
	 * Sets the type (video or sound) of the media object
	 * this is only a visual representation for the drag and drop
	 * @param type
	 */
	public void setType (MediaObjectType type) {
		mType = type;
		if(mType == MediaObjectType.SOUND){
//			getStyleClass();
		}else{
//			getStyleClass().add();
		}
		
	}
	
	public void buildNodeDragHandlers() {
		
		mContextDragOver = new EventHandler <DragEvent>() {

			//dragover to handle node dragging in the right pane view
			@Override
			public void handle(DragEvent event) {		
		
				event.acceptTransferModes(TransferMode.ANY);				
				relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));

				event.consume();
			}
		};
		
		//dragdrop for node dragging
		mContextDragDropped = new EventHandler <DragEvent> () {
	
			@Override
			public void handle(DragEvent event) {
			
				getParent().setOnDragOver(null);
				getParent().setOnDragDropped(null);
				
				event.setDropCompleted(true);
				
				event.consume();
			}
		};
		//close button click
//		close_button.setOnMouseClicked( new EventHandler <MouseEvent> () {
//
//			@Override
//			public void handle(MouseEvent event) {
//				AnchorPane parent  = (AnchorPane) self.getParent();
//				parent.getChildren().remove(self);
//			}
//			
//		});
		
		//drag detection for node dragging
		setOnDragDetected ( new EventHandler <MouseEvent> () {

			@Override
			public void handle(MouseEvent event) {
			
				getParent().setOnDragOver(null);
				getParent().setOnDragDropped(null);

				getParent().setOnDragOver (mContextDragOver);
				getParent().setOnDragDropped (mContextDragDropped);

                //begin drag ops
                mDragOffset = new Point2D(event.getX(), event.getY());
                
                relocateToPoint(
                		new Point2D(event.getSceneX(), event.getSceneY())
                		);
                
                ClipboardContent content = new ClipboardContent();
//				DragContainer container = new DragContainer();
				
//				container.addData ("type", mType.toString());
//				content.put(DragContainer.AddNode, container);
				
                startDragAndDrop (TransferMode.ANY).setContent(content);                
                
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
