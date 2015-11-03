/**
 * 
 */
package gui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import modules.*;

/**
 * @author Anders Lunde
 * This is the icon that is being displayed when the media object is being dragged
 */
public class MediaObjectIcon extends GridPane{
	@FXML GridPane grid_pane;
	private @FXML Label nameOfFile;
	private @FXML Region styleIcon;

	private Tooltip mediaTooltip = new Tooltip();

	//Drag&Drop
	private EventHandler <DragEvent> mContextDragOver;
	private EventHandler <DragEvent> mContextDragDropped;
	private Point2D mDragOffset = new Point2D (0.0, 0.0);
	private MediaSourceType mType = null;
	
	private final ContextMenu contextMenu = new ContextMenu();
	private Alert alert;

	private MediaObject mediaObject;

	public MediaObjectIcon(MediaObject mediaObject) {

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

		this.mediaObject = mediaObject;
		initializeTooltip();
		initializeMouse();

	}

	private void initializeTooltip(){
		//Sets the text of the tooltip
		if(mediaObject != null){
			mediaTooltip.setText(
				"Name: "+mediaObject.getName()+"\n" +
						"Duration: "+Long.toString(mediaObject.getLength()/1000)+" Seconds\n"
				);
		nameOfFile.setTooltip(mediaTooltip);
		}
	}

	/**
	 * Adds the right click functions to the MediaObject, edit, remove. and puts an event handler onto them.
	 */
	private void initializeMouse(){
		initializeAlerts();
		//Adds different right click options to the ContextMenu that pops up on mouse click.
		MenuItem remove = new MenuItem("Remove");
		MenuItem setPath = new MenuItem("Set path");
		contextMenu.getItems().add(remove);
		contextMenu.getItems().add(setPath);
		remove.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		        System.out.println("Remove MediaObject");
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					//TODO: Remove
//					AdvancedScreen.getInstance().getScreenController().getCurrentSession().removeTimelineMediaObjectFromTimeline(parentController.getParentController().getTimelineModel(),timelineMediaObject);
					AdvancedScreen.getInstance().getScreenController().getCurrentSession().removeMediaObject(mediaObject);
				} else {
				    // ... user chose CANCEL or closed the dialog
				}
		        
		    }
		});
		setPath.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Set path MediaObject");
				if(mediaObject.getType()==MediaSourceType.WINDOW){
					AdvancedScreen.getInstance().getScreenController().showWindowChooser(mediaObject);
				}
				else{
					FileChooser fileChooser;
					File selectedFile = null;
					try {
						fileChooser = new FileChooser();
						fileChooser.setTitle("Choose file");
						selectedFile = fileChooser.showOpenDialog(MainGUIController.getInstance().primaryStage); //Possibilty to select more files at the time
					} catch(Exception e) {
						System.out.println("[MediaObjectIcon.initializeMouse(): setPath.setOnAction] FileChooser caught an expection");
					}
				//	checks for aborted file import
					if(selectedFile==null){
						return;
					}
					
					AdvancedScreen.getInstance().getScreenController().getCurrentSession().changeMediaObject(mediaObject, selectedFile.getAbsolutePath());
				}
					
			}
		});
		grid_pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			 
            @Override
            public void handle(MouseEvent event) {
            	contextMenu.hide();
//            	parentController.getContextMenu().hide();
                MouseButton button = event.getButton();
                if(button==MouseButton.SECONDARY){
                    System.out.println("Right Cliked a MediaObject");
                    contextMenu.show(grid_pane, event.getScreenX(), event.getScreenY());
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
	
	/*
	 * Method for setting the title of the Icon
	 */
	public void setTitle(String s){
		nameOfFile.setText(s);
	}

	@FXML
	private void initialize() {
		buildNodeDragHandlers();
	}
	public void setMediaObject(MediaObject mediaObject){
		//Check validity
		this.mediaObject=mediaObject;
	}

	public void buildNodeDragHandlers() {

		mContextDragOver = new EventHandler <DragEvent>() {

			//dragover to handle node dragging inside the timeline
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
		//	close_button.setOnMouseClicked( new EventHandler <MouseEvent> () {
		//
		//		@Override
		//		public void handle(MouseEvent event) {
		//			AnchorPane parent  = (AnchorPane) self.getParent();
		//			parent.getChildren().remove(self);
		//		}
		//		
		//	});

		//drag detection for node dragging
		//TODO: use like a title bar for dragging ? not dragging the whole ting ?
		//	title_bar.setOnDragDetected ( new EventHandler <MouseEvent> () {
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
				MediaObjectContainer container = new MediaObjectContainer();

				container.addData ("type", mType.toString());
				content.put(MediaObjectContainer.AddNode, container);

				startDragAndDrop(TransferMode.ANY).setContent(content);                

				event.consume();					
			}

		});		
	}	

	public void relocateToPoint (Point2D p) {


		//relocates the object to a point that has been converted to
		//scene coordinates
		Point2D localCoords = getParent().sceneToLocal(p);

		relocate ( 
				(int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
				(int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
				);
	}

	public MediaSourceType getType () {
		return mType; 
	}

	public void setType (MediaSourceType type) {

		mType = type;
		System.out.println("Media TYPE: " + type);
		//		getStyleClass().clear();
		//		getStyleClass().add("dragicon");

		if(mType == MediaSourceType.VIDEO){
			styleIcon.getStyleClass().add("icon-video");
		}else if (mType == MediaSourceType.AUDIO){
			styleIcon.getStyleClass().add("icon-sound");
		}else if (mType == MediaSourceType.IMAGE){
			styleIcon.getStyleClass().add("icon-image");
		}else if (mType == MediaSourceType.WINDOW){
			styleIcon.getStyleClass().add("icon-window");
		}else{
			styleIcon.getStyleClass().add("icon-video");
		}

	}

	public MediaObject getMediaObject(){
		return mediaObject;
	}
}
