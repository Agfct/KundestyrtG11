/**
 * 
 */
package gui;

import java.io.IOException;
import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * @author Anders
 * This is the visual representation of the MediaObject (Movie, Sound)
 */
public class MediaObjectController extends GridPane{

	private FXMLLoader fxmlLoader;
	private TimelineLineController parentController;
	private GridPane root = this;

	//Variables used for dragging/dropping
	private AnchorPane masterRootPane;
	private EventHandler <DragEvent> mContextDragOver;
	private EventHandler <DragEvent> mContextDragDone;
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
		
		
		//Sets the master root pane for drag and drop
		masterRootPane = AdvancedScreen.getInstance().getScreenController().getMasterRoot();
	}
	
	/**
	 * Extracts the information from the container and adds it to the mediaObjectController
	 * @param container
	 */
	public void initializeMediaObject(MediaObjectContainer container){
		setType(MediaObjectType.valueOf(container.getValue("type")));
	}
	
	/**
	 * This method is ran when the object is initialized (created) i the FXML
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
		
		getStyleClass().clear();
		getStyleClass().add("dragicon");
		
		if(mType == MediaObjectType.SOUND){
			getStyleClass().add("icon-sound");
		}else{
			getStyleClass().add("icon-video");
		}
		
	}
	
	public void buildNodeDragHandlers() {
		

		/**
		 * This is the method for handling dragging over the same
		 * Parent
		 */
		mContextDragOver = new EventHandler <DragEvent>() {

			//dragover to handle dragging the MediaObject
			//TODO: needs to add correct coords and movement of the mouse (only left and right)
			@Override
			public void handle(DragEvent event) {		
//				System.out.println("[MediaObjectController] Dargging over root");
				
				AnchorPane timelineLinePane = parentController.getRoot();
				Point2D p = timelineLinePane.sceneToLocal(event.getSceneX(), event.getSceneY());
				
//				System.out.println("[MediaObject] sceneX: "+event.getSceneX()+" LocalX: "+ p.getX());
//				System.out.println("[MediaObject] LocalX: "+p.getX()+" LocalX: "+ p.getY());
//				System.out.println("[MediaObjectController] AnchtorPane Bounds: "+timelineLinePane.boundsInLocalProperty().get());
				
				//Prevents you from dragging outside timeline boundaries
				Bounds boundsInParent = getBoundsInParent();
				Bounds newBounds = new BoundingBox(p.getX() - mDragOffset.getX(),p.getY() - mDragOffset.getY(),
					(p.getX() - mDragOffset.getX()) + boundsInParent.getWidth(), (p.getY() - mDragOffset.getY()) + boundsInParent.getHeight());
//				System.out.println("[MediaObjectController] NewBounds for MediaObject: " +newBounds);
				
				if (timelineLinePane.getBoundsInLocal().contains(newBounds)) {
//					if (timelineLinePane.boundsInLocalProperty().get().intersects(root.getLayoutX(), root.getLayoutY(), root.getLayoutX() + root.getWidth(), root.getLayoutY()+ root.getHeight())) {
//					System.out.println("[MediaObject], yes its TRUE p is inside the panel");
					event.acceptTransferModes(TransferMode.MOVE);
					relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
//					System.out.println("[MediaObjectController New moved Location mediaObject" + root.getLocalToParentTransform());
//					relocateToPoint(new Point2D(event.getSceneX(), 0));
				}
//				event.acceptTransferModes(TransferMode.ANY);				
//				relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));

				event.consume();
			}
		};
		
		/**
		 * This is the method for handling dropping of the MediaObject
		 */
		mContextDragDropped = new EventHandler <DragEvent> () {
	
			@Override
			public void handle(DragEvent event) {
			
//				parentController.getRoot().setOnDragOver(null);
//				parentController.getRoot().setOnDragDropped(null);
//				
				event.setDropCompleted(true);
				
				event.consume();
			}
		};
		
		mContextDragDone = new EventHandler <DragEvent> () {
			
			@Override
			public void handle (DragEvent event) {
				System.out.println("[MediaObjectController] Drag DONE");
				
				parentController.getRoot().removeEventHandler(DragEvent.DRAG_OVER, mContextDragOver);
				parentController.getRoot().setOnDragOver(null);
				parentController.getRoot().setOnDragDropped(null);
				parentController.getRoot().setOnDragDone(null);

				
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
		
		/**
		 * When you drag the MediaObject it triggers setOnDragDetected
		 */
		setOnDragDetected ( new EventHandler <MouseEvent> () {

			@Override
			public void handle(MouseEvent event) {
			
				/* Drag was detected, start a drag-and-drop gesture */
				/* allow any transfer mode */
				System.out.println("[MediaObjectController] Drag event started");
				
//				getParent().setOnDragOver(null);
//				getParent().setOnDragDropped(null);
//
//				getParent().setOnDragOver (mContextDragOver);
//				getParent().setOnDragDropped (mContextDragDropped);
//				parentController.getRoot().setOnDragOver(null);
//				parentController.getRoot().setOnDragDropped(null);

				
				parentController.getRoot().setOnDragOver (mContextDragOver);
				parentController.getRoot().setOnDragDropped (mContextDragDropped);
				parentController.getRoot().setOnDragDone(mContextDragDone);

                //begin drag ops
                mDragOffset = new Point2D(event.getX(), event.getY());
                
                relocateToPoint(
                		new Point2D(event.getSceneX(), event.getSceneY())
                		);
                
                //The clipboard contains all content that are to be transfered in the drag
                ClipboardContent content = new ClipboardContent();
                
                //creating a container with all the data of the media object
				MediaObjectContainer container = new MediaObjectContainer();			
				container.addData ("type", mType.toString());
                
                //Putting the data container onto the content
				content.put(MediaObjectContainer.DragNode, container); //TODO: AddNode ??
				
                startDragAndDrop (TransferMode.MOVE).setContent(content);      
                
                event.consume();					
			}
			
		});
		
	}
	
//	public Rectangle getRect(){
//		Rectangle tempRect = new Rectangle(x,y,width,height);
//		return tempRect;
//		
//	}
//	public Bounds getBounds(){
//		Bounds tempBounds = new Bounds;
//		return tempBounds;
//	}

	/**
	 * @return the parentController
	 */
	public TimelineLineController getParentController() {
		return parentController;
	}

	/**
	 * @param parentController the parentController to set
	 */
	public void setParentController(TimelineLineController parentController) {
		this.parentController = parentController;
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
