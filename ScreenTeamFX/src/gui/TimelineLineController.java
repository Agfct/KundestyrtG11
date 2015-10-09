package gui;

import java.io.IOException;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * 
 * @author Anders Lunde
 * The TimelineLineController is the controller of the line you see at the right side, 
 * containing all the media objects.
 */
public class TimelineLineController implements FXMLController{
	
	
	private FXMLLoader fxmlLoader;
	private TimelineController parentController;
	private AnchorPane rootPane;
	
	/**
	 * 
	 * @param parentController
	 */
	public TimelineLineController(TimelineController parentController){
		
		//Fetches the parent controller. In this case it is the controller in the advancedScreen class.'
		this.parentController = parentController;
		
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
		//TODO: REMOVE, TESTING ONLY
//		rootPane.getChildren().add(new MediaObjectController());
//		
//		rootPane.setOnDragOver(new EventHandler<DragEvent>() {
//		    public void handle(DragEvent event) {
//		    	System.out.println("Dropped");
//		        /* data is dragged over the target */
//		        /* accept it only if it is not dragged from the same node 
//		         * and if it has a string data */
////		        if (event.getGestureSource() != this &&
////		                event.getDragboard().hasString()) {
////		            /* allow for both copying and moving, whatever user chooses */
////		            event.acceptTransferModes(TransferMode.MOVE);
////		        }
//		        if (event.getGestureSource() != this){
//		        	System.out.println("Dropped");
//		        event.acceptTransferModes(TransferMode.MOVE);
//		        }
//		        
//		        event.consume();
//		    }
//		});
//		//end of TEST
		
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
	
	/**
	 * Receives and add/places a mediaObject to the timelineLine based on the coordinates p
	 * @param node
	 * @param p
	 */
	public void addMediaObject(MediaObjectController node, Point2D p) {
		rootPane.getChildren().add(node); //TODO: REMOVE TEMPorarly fix
		
	}

}
