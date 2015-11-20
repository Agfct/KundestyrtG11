/**
 * 
 */
package gui;

import java.io.IOException;
import java.util.Optional;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import modules.MediaSourceType;
import modules.TimelineMediaObject;

/**
 * @author Anders Lunde, Magnus Gundersen
 * This is the visual representation of the MediaObject (Movie, Sound, etc)
 * It contains its own timelineMediaObject which is the real model from the modules.
 * The class also handles the drag&drop of the MediaObject
 */
public class MediaObjectController extends GridPane{

	private FXMLLoader fxmlLoader;
	private TimelineLineController parentController;
	private GridPane root = this;
	private final MediaObjectController thisMediaObject = this;
	private final ContextMenu contextMenu = new ContextMenu();
	private Alert alert;
	private Tooltip mediaTooltip = new Tooltip();

	//Variables used for dragging/dropping
	private EventHandler <DragEvent> mContextDragOver;
	private EventHandler <DragEvent> mContextDragDone;
	private EventHandler <DragEvent> mContextDragDropped;
	private Point2D mDragOffset = new Point2D (0.0, 0.0);
	
	//Model corresponding to this controller
	private TimelineMediaObject timelineMediaObject;
	
	//Width and height of the mediaObject, the width is changed by the modules.
	//Height is based on the mediaObject.fxml height.
	private double mediaObjectActualWidth = 1000;
	private double mediaObjectHeigth = 70;
	
	private @FXML Label nameOfFile;
	
	public MediaObjectController(TimelineMediaObject timelineMediaObject){
//		setStyle("-fx-background-color: GRAY");
		setGraphicType(timelineMediaObject.getParent().getType());
		this.timelineMediaObject=timelineMediaObject;
		
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("MediaObject.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.setRoot(this);
			fxmlLoader.load();
		} catch (IOException e) {
			System.out.println("Failed to load MediaObject FXML");
			e.printStackTrace();
		}
		
		//initialize drag&drop
		initializeMouse();
		
	}
	
	/**
	 * Extracts the information from the container and adds it to the mediaObjectController
	 * @param container
	 */
	public void initializeMediaObject(){
		this.nameOfFile.setText(timelineMediaObject.getParent().getName());
		setMediaObjectWidth();
		
		//Sets the text of the tooltip
		mediaTooltip.setText(
			    "Name: "+timelineMediaObject.getParent().getName()+"\n" +
			    "StartTime: "+getStartTimeAsText()+"\n" +
			    "Duration: "+Long.toString(timelineMediaObject.getDuration()/1000)+" Seconds\n"
			);
		nameOfFile.setTooltip(mediaTooltip);
		
	}
	
	public void updateValuesFromModel(){
		setMediaObjectWidth();
		
		//Updates the text of the tooltip
		mediaTooltip.setText(
			    "Name: "+timelineMediaObject.getParent().getName()+"\n" +
			    "StartTime: "+getStartTimeAsText()+"\n" +
			    "Duration: "+Long.toString(timelineMediaObject.getDuration()/1000)+" Seconds\n"
			);
		nameOfFile.setTooltip(mediaTooltip);

	}
	
	private String getStartTimeAsText(){
		long timeInSeconds = timelineMediaObject.getStart()/1000;
		long hours = timeInSeconds/3600;
		long minutes = (timeInSeconds % 3600) / 60;
		long seconds = timeInSeconds % 60;;
	    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	/**
	 * This method sets the width of the mediaObject based on the zoom scale
	 * @param width
	 */
	private void setMediaObjectWidth(){
		int tempScale = AdvancedScreen.getInstance().getScreenController().getScaleCoefficient();
		this.mediaObjectActualWidth=Math.ceil((((timelineMediaObject.getDuration()*tempScale)/1000)+0.5));
		this.setPrefWidth(mediaObjectActualWidth);
		this.setMaxWidth(mediaObjectActualWidth);
	}
	
	/**
	 * This method is ran when the object is initialized (created) i the FXML
	 */
	@FXML
	private void initialize() {
		buildNodeDragHandlers();
	}
	
	/**
	 * Adds the right click functions to the MediaObject, edit, remove. and puts an event handler onto them.
	 */
	private void initializeMouse(){
		initializeAlerts();
		//Adds different right click options to the ContextMenu that pops up on mouse click.
		MenuItem edit = new MenuItem("Edit");
		MenuItem remove = new MenuItem("Remove");
		contextMenu.getItems().addAll(edit, remove);
		remove.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		        System.out.println("Remove MediaObject");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
				    // ... user chose OK
//					parentController.removeMediaObject(thisMediaObject);
					AdvancedScreen.getInstance().getScreenController().getCurrentSession().removeTimelineMediaObjectFromTimeline(parentController.getParentController().getTimelineModel(),timelineMediaObject);
				} else {
				    // ... user chose CANCEL or closed the dialog
				}
		        
		    }
		});
		edit.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		        System.out.println("Edit MediaObject");
				AdvancedScreen.getInstance().getScreenController().showModal(thisMediaObject);
		    }
		});
		root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			 
            @Override
            public void handle(MouseEvent event) {
            	contextMenu.hide();
            	parentController.getContextMenu().hide();
                MouseButton button = event.getButton();
                if(button==MouseButton.SECONDARY){
                    System.out.println("Right Cliked a MediaObject");
                    contextMenu.show(root, event.getScreenX(), event.getScreenY());
                }
                event.consume(); //Consumes the event so it wont go deeper down into the hierarchy 
            }
        });
		

	}
	
	/**
	 *Initializes the alert box that is displayed when the user right clicks the mediaObject and presses remove.
	 */
	private void initializeAlerts(){
		alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Delete MediaObject");
		alert.setContentText("Do you really want to delete this MediaObject?");
	}
	
	public MediaSourceType getType () { 
		return timelineMediaObject.getParent().getType(); 
		}
	
	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		relocate ( 
		(int) (p.getX()),
		(int) (p.getY())
	);
	}
	
	/**
	 * Sets the type (video or sound) of the media object
	 * this is only a visual representation for the drag and drop
	 * @param type
	 */
	public void setGraphicType (MediaSourceType type) {
		
//		getStyleClass().clear();
//		getStyleClass().add("dragicon");
//		
		if(type == MediaSourceType.AUDIO){
			getStyleClass().add("background-sound");
		}else if(type == MediaSourceType.IMAGE){
			getStyleClass().add("background-image");
		}else if(type == MediaSourceType.WINDOW){
			getStyleClass().add("background-window");
		}else{
			getStyleClass().add("background-video");
		}
		
	}
	
	public void buildNodeDragHandlers() {
		
		
		/**
		 * This is the method for handling dragging the mediaObject
		 * It takes the coords of the timelinePane and creates a rectangle (Bounds) and checks if it is inside the bounds of the timelinePane
		 * NB: The y in the x,y coords are always 0 to prevent any vertical movement.
		 */
		mContextDragOver = new EventHandler <DragEvent>() {

			//dragover to handle dragging the MediaObject
			@Override
			public void handle(DragEvent event) {		
//				System.out.println("[MediaObjectController] Dargging over root");
				
				AnchorPane timelineLinePane = parentController.getRoot();
				Point2D p = timelineLinePane.sceneToLocal(event.getSceneX(), event.getSceneY());
				Bounds mediaControllerRect;
				if(p.getX() - mDragOffset.getX() >= 0){
					 mediaControllerRect = new BoundingBox(p.getX() - mDragOffset.getX(),0,
						getMediaObjectWidth(), getMediaObjectHeigth());
				}else{
					 mediaControllerRect = new BoundingBox(0,0,
							getMediaObjectWidth(), getMediaObjectHeigth());
				}
				
				if (timelineLinePane.getBoundsInLocal().contains(mediaControllerRect)) {
					event.acceptTransferModes(TransferMode.MOVE);
					relocateToPoint(new Point2D(mediaControllerRect.getMinX(),mediaControllerRect.getMinY()));
				}

				event.consume();
			}
		};
		
		/**
		 * This is the method for handling dropping of the MediaObject
		 */
		mContextDragDropped = new EventHandler <DragEvent> () {
	
			@Override
			public void handle(DragEvent event) {
			
				event.setDropCompleted(true);
				
				event.consume();
			}
		};
		
		mContextDragDone = new EventHandler <DragEvent> () {
			
			@Override
			public void handle (DragEvent event) {
				System.out.println("[MediaObjectController] Drag DONE");
				
				AdvancedScreenController tempAdvSrcController = AdvancedScreen.getInstance().getScreenController();
				
				parentController.getRoot().removeEventHandler(DragEvent.DRAG_OVER, mContextDragOver);
				parentController.getRoot().setOnDragOver(null);
				tempAdvSrcController.getMasterRoot().removeEventHandler(DragEvent.DRAG_OVER, mContextDragOver);
				tempAdvSrcController.getMasterRoot().setOnDragOver(null);
				parentController.getRoot().setOnDragDropped(null);
				parentController.getRoot().setOnDragDone(null);

				tempAdvSrcController.getCurrentSession().timelineMediaObjectChanged(thisMediaObject.getParentController().getParentController().getTimelineModel(),thisMediaObject.getTimelineMediaObject(),(int)((thisMediaObject.getLayoutX()*1000)/tempAdvSrcController.getScaleCoefficient()),(int)thisMediaObject.getTimelineMediaObject().getStartPoint(),(int)thisMediaObject.getTimelineMediaObject().getDuration());
			}
		};
		
		
		/**
		 * When you drag the MediaObject it triggers setOnDragDetected
		 */
		setOnDragDetected ( new EventHandler <MouseEvent> () {

			@Override
			public void handle(MouseEvent event) {
			
				/* Drag was detected, start a drag-and-drop gesture */
				/* allow any transfer mode */
				System.out.println("[MediaObjectController] Drag event started");
				
				AdvancedScreen.getInstance().getScreenController().getMasterRoot().setOnDragOver (mContextDragOver);
				
				parentController.getRoot().setOnDragOver (mContextDragOver);
				parentController.getRoot().setOnDragDropped (mContextDragDropped);
				parentController.getRoot().setOnDragDone(mContextDragDone);

                //begin drag ops
                mDragOffset = new Point2D(event.getX(), event.getY());
                System.out.println("dragOffset with getX: " + mDragOffset);
                System.out.println("relocate to point with sceneX with getX: " + (new Point2D(event.getSceneX(), event.getSceneY())));
                
                //The clipboard contains all content that are to be transfered in the drag
                ClipboardContent content = new ClipboardContent();
                
                //creating a container with all the data of the media object
				MediaObjectContainer container = new MediaObjectContainer();			
//				container.addData ("type", timelineMediaObject.getParent().getType().toString());
                
                //Putting the data container onto the content
				content.put(MediaObjectContainer.DragNode, container);
				
                startDragAndDrop (TransferMode.MOVE).setContent(content);      
                
                event.consume();					
			}
			
		});
		
	}

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
	
	private double getMediaObjectWidth(){
		return mediaObjectActualWidth;
	}
	
	private double getMediaObjectHeigth(){
		return mediaObjectHeigth;
	}
	
	public TimelineMediaObject getTimelineMediaObject(){
		return timelineMediaObject;
	}
}
