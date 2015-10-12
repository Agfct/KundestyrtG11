package gui;

import java.io.IOException;
import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;
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
	
	@FXML Button removeTimelineBtn;
	@FXML GridPane timelineInfo;
	@FXML GridPane timelineContainer;
//	@FXML AnchorPane timelineLineContainer;
	
	
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

	
	/**
	 * This method is ran when you press a button in the TimelineInfo screen (Left side of timeline).
	 * It assumes that all buttons has in id. if they do not have an id this method gives a null pointer exception.
	 * @param event
	 */
	@FXML protected void buttonPressed(ActionEvent event) {
		System.out.println("AdvancedScreen:" + event.getSource().toString() + "has been pressed");
		
		if(((Button)event.getSource()).getId().equals("removeTimelineBtn") ){
			//Removes the timeline from the AdvanceScreen Children list
			parentController.removeTimeline(this);
			
		}
		
		
	}
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
	
	public GridPane getRoot(){
		return rootPane;
	}
	
	public GridPane getTimelineInfo(){
		return timelineInfo;
	}


	
	

}
