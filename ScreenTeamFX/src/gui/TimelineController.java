package gui;

import java.io.IOException;
import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.GridPane;

/**
 * 
 * @author Anders Lunde
 * The TimelineController is the controller of everything you see in a single timeline.
 * It contains fxml for the timeline info (left side) and the timeline with videos (right side)
 */
public class TimelineController implements FXMLController {
	
	private FXMLLoader fxmlLoader;
	private AdvancedScreenController parentController;
	private TimelineLineController childController;
	private GridPane rootPane;
	
	
	
	/**
	 *TODO: Add a timelineModel to this controller, and also add its model the the module.
	 *
	 *This construction initiates the TimelineInfo.fxml and controls the actions from it.
	 *It also creates an instance of the TimeLineLineController which contains the TimelineLine.fxml.
	 */
	public TimelineController (){
		
		//Fetches the parent controller. In this case it is the controller in the advancedScreen class.'
		parentController = AdvancedScreen.getInstance().getScreenController();
		
		// The constructor will try to fetch the fxml 
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("TimelineInfo.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.load();
			rootPane = fxmlLoader.getRoot();
		} catch (IOException e) {
			System.out.println("Failed to load TimelineController FXML");
			e.printStackTrace();
		}

		//Initialize drag and drop
//		initialize();
		
		//Creates the actual timeline line (right side of the timeline) using this controller as parent controller
		childController = new TimelineLineController(this);
		
	}
	
//	private void initialize(){
//		
//		
//		//Drag&drop functionality
//		mDragOverIcon = new MediaObjectIcon();
//		mDragOverIcon.setVisible(false);
//		mDragOverIcon.setOpacity(0.65);
//		rootPane.getChildren().add(mDragOverIcon);
//	}

	/* (non-Javadoc)
	 * @see gui.FXMLController#getFXMLLoader()
	 */
	@Override
	public FXMLLoader getFXMLLoader() {
		return fxmlLoader;
	}
	
	public TimelineLineController getTimelineLineController(){
		return childController;
	}

	/**
	 * Receives and add/places a mediaObject to the timeline based on the coordinates p
	 * @param node
	 * @param p
	 */
	public void addMediaObject(MediaObjectController node, Point2D p) {
		rootPane.getChildren().add(node); //TODO: REMOVE TEMP
		
	}
	
	

}
