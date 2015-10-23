/**
 * 
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import modules.MainModuleController;

/**
 * @author Anders Lunde
 *
 */
public class TimelineBarController extends Pane {
	
	private FXMLLoader fxmlLoader;
	private AdvancedScreenController parentController;
	
	private @FXML Canvas timelineBarCanvas;
	private GraphicsContext gc;
	private Pane root = this;

	private SeekerController seeker;
	
	public TimelineBarController(AdvancedScreenController parentController){
	//Fetches the parent controller. In this case it is the controller in the advancedScreen class.'
	this.parentController = parentController;
	
//	this.setStyle("-fx-background-color:RED");
	
	// The constructor will try to fetch the fxml 
	try {
		fxmlLoader = new FXMLLoader(getClass().getResource("TimelineBar.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		fxmlLoader.load();
	} catch (IOException e) {
		System.out.println("Failed to load TimelineBar FXML");
		e.printStackTrace();
	}
	
	initializeTimeBar();
	
	seeker = new SeekerController(this);
	this.getChildren().add(seeker);
	
	}
	
	
	/**
	 * Creates the canvas that will be drawn upon in the timelineBar
	 */
	public void initializeTimeBar(){
		long sessionLength = MainModuleController.getInstance().getSession().getSessionLength();
		long widthBetweenLines = 10;
		timelineBarCanvas.setWidth(sessionLength);
		gc = timelineBarCanvas.getGraphicsContext2D() ;
		gc.setLineWidth(1.0);
	     for (int x = 0; x < sessionLength; x+=widthBetweenLines) {
	            double x1 ;
	            x1 = x + 0.5 ; //TODO: The 0.5 is to get a clean (not blurry) line, but it might mean that x width should be +1 more pixel
	            gc.moveTo(x1, 25);
	            gc.lineTo(x1, 15);
	            gc.stroke();
	            gc.setFont(new Font(8));
	            gc.fillText("1", x1+1, 12);
//	            gc.strokeText("1", x1, 12);
	        }
	     
			//Creates a clipping mask to hide timelineBar outside of bounds
			createClip();
	}
	
	/**
	 * Creates a clip that decides the viewport of this Panes children
	 * The timelineBar that is outside of this clip will be hidden for the user.
	 */
	private void createClip(){
		Rectangle clipSize = new Rectangle(1000,150);
		clipSize.setLayoutX(0);
		clipSize.setLayoutY(0);
		parentController.getTimelineBarContainer().setClip(clipSize);
	}
	
	protected void moveTimelineBar(Double newPosition){
		root.setLayoutX(newPosition);
	}
	
	public Pane getRoot(){
		return root;
	}
	
	public AdvancedScreenController getAdvancedScreenController(){
		return parentController;
	}
	


}
