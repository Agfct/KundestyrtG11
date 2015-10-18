package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import modules.MediaSourceType;
import modules.TimelineMediaObject;
import modules.TimelineModel;
import uk.co.caprica.vlcj.binding.internal.media_duration_changed;

/**
 * 
 * @author Anders Lunde, Magnus Gundersen
 * The TimelineLineController is the controller of the line you see at the right side, 
 * containing all the media objects.
 * The controller handles all internal drag and drop operations
 */
public class TimelineLineController implements FXMLController{
	
	
	private FXMLLoader fxmlLoader;
	private TimelineController parentController;
	private AnchorPane rootPane;
	private final ContextMenu contextMenu = new ContextMenu();

	
	//Drag&drop
//	private MediaObjectIcon mDragOverIcon = null;
//	private EventHandler<DragEvent> mIconDragOverRoot = null;
//	private EventHandler<DragEvent> mIconDragDropped = null;
	
	//TODO: dummy list ? should it be this way ?
	private ArrayList<MediaObjectController> mediaObjectControllers;
	private ArrayList<TimelineMediaObject> timelineMediaObjectModels;
	private HashMap<TimelineMediaObject,MediaObjectController> mediaObjectToControllerMap;
	
	/**
	 * 
	 * @param parentController
	 */
	public TimelineLineController(TimelineController parentController){
		
		//Fetches the parent controller. In this case it is the timelineController who has this timelinelinecontroller
		this.parentController = parentController;
		
		//initializes empty mediaObjectList
		mediaObjectControllers = new ArrayList<>();
		
		// The constructor will try to fetch the fxml 
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("TimelineLine.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.load();
			rootPane = fxmlLoader.getRoot();
		} catch (IOException e) {
			System.out.println("Failed to load TimelineLine FXML");
			e.printStackTrace();
		}
		

		
		
		//Mouse Functionallity
		initializeMouse();
		
		//Puts this TimelineLine onto the info (parent) controller
//		parentController.timelineContainer.add(this.rootPane, 1, 0);
		parentController.timelineLineContainer.getChildren().add(this.rootPane);
		
		//Drag&drop functionality
		
	}
	
	/**
	 * Initializes all the mouse gesture controls, and also initializes the right click pop up menu.
	 */
	private void initializeMouse(){
		//Adds different right click options to the ContextMenu that pops up on mouse click.
		MenuItem cut = new MenuItem("Cut");
		MenuItem copy = new MenuItem("Copy");
		MenuItem paste = new MenuItem("Paste");
		contextMenu.getItems().addAll(cut, copy, paste);
		cut.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		        System.out.println("Cut...");
		    }
		});
		rootPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			 
            @Override
            public void handle(MouseEvent event) {
            	contextMenu.hide();
                MouseButton button = event.getButton();
                if(button==MouseButton.PRIMARY){
                    System.out.println("PRIMARY button clicked");
                }else if(button==MouseButton.SECONDARY){
                	System.out.println("SECONDARY button clicked");
                	contextMenu.show(rootPane, event.getScreenX(), event.getScreenY());
                }else if(button==MouseButton.MIDDLE){
                	System.out.println("MIDDLE button clicked");
                }
                event.consume(); //Consumes the event so it wont go deeper down into the hierarchy 
            }
        });

	}


	/* (non-Javadoc)
	 * @see gui.FXMLController#getFXMLLoader()
	 */
	@Override
	public FXMLLoader getFXMLLoader() {
		return fxmlLoader;
	}
	
	public void setRootOnDragOver( EventHandler<DragEvent> event){
		rootPane.setOnDragOver(event);
	}
	
	public void setRootOnDropped( EventHandler<DragEvent> event){
		rootPane.setOnDragDropped(event);
	}
	
	public AnchorPane getRoot(){
		return rootPane;
	}
	
	public ContextMenu getContextMenu(){
		return contextMenu;
	}
	
	public TimelineController getParentController(){
		return parentController;
	}
	
	/**
	 * Receives and add/places a mediaObject to the timelineLine based on the coordinates p
	 * @param node
	 * @param p
	 */
	public void addMediaObject(MediaObjectController node, Point2D p) {
		rootPane.getChildren().add(node); //TODO: REMOVE TEMPorarly fix
		mediaObjectControllers.add(node);
		node.setParentController(this);
		node.relocateToPoint(p);
		
	}
	
	//TODO: REVISIT TESTING ATM
	public void removeMediaObject(MediaObjectController node) {
		rootPane.getChildren().remove(node); //TODO: REMOVE TEMPorarly fix
		mediaObjectControllers.remove(node);
		
	}
	
	protected void moveTimeline(Double newPosition){
//		System.out.println("TimelineLineController Moving the Root");
		rootPane.setLayoutX(newPosition);
	}

	public void repaint() {
		System.out.println("---Repainting---");
		// TODO Go through all mediaObjectControllers, and repaint according to the new model. 
		rootPane.getChildren().clear();
		mediaObjectControllers.clear();
		
		TimelineModel model=parentController.getTimelineModel();
		timelineMediaObjectModels=model.getTimelineMediaObjects();
		for(TimelineMediaObject tlmo:timelineMediaObjectModels){
			MediaObjectController mediaObjectController = new MediaObjectController(tlmo);
			mediaObjectController.initializeMediaObject();
			addMediaObject(mediaObjectController, new Point2D(tlmo.getStart()/1000, 0));
			
		}
		
		for(MediaObjectController mediaObjectController:mediaObjectControllers){
			//TODO: run updateValueFromModel on each controller?
			mediaObjectController.updateValuesFromModel();
		}
		System.out.println("Goon do some repainting");
		
	}

}
