/**
 * 
 */
package gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * @author Anders Lunde
 *
 */
public class StopPointController extends Pane{
	private FXMLLoader fxmlLoader;
	private TimelineBarController parentController;
	private Pane root = this;
	private StopPointController thisStopPoint = this;

	//Tooltip
	private @FXML Label hoverOverLabel; 
	private Tooltip mediaTooltip = new Tooltip();

	//scaling
	private int scale = 1;

	private final ContextMenu contextMenu = new ContextMenu();
	private Point2D seekPoint;


	public StopPointController(TimelineBarController parent){
		parentController = parent;


		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("StopPoint.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.setRoot(this);
			fxmlLoader.load();
		} catch (IOException e) {
			System.out.println("Failed to load Stop point FXML");
			e.printStackTrace();
		}

		initializeMouse();
		initializeTooltip();
	}

	public void setStopPosition(Point2D point){
		//		Point2D localXY = parentToLocal(point.getX(), point.getY());
		relocateToPoint(new Point2D((point.getX()- 2),0));
		initializeTooltip();
		System.out.println("Stop point point: " + this.getLayoutX());

	}

	private void initializeTooltip(){
		//Sets the text of the tooltip
		mediaTooltip.setText(
						"StartTime: "+getStartTimeAsText()+"\n"
				);
		hoverOverLabel.setTooltip(mediaTooltip);
	}

	private void initializeMouse(){
		//Adds different right click options to the ContextMenu that pops up on mouse click.
		MenuItem moveSeeker = new MenuItem("MoveSeeker");
		MenuItem removeStop = new MenuItem("RemoveStop");
		contextMenu.getItems().addAll(moveSeeker,removeStop);
		moveSeeker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Moving seeker");
				parentController.moveSeeker(seekPoint);
			}
		});
		removeStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Removing Stop point");
				parentController.removeStopPoint(thisStopPoint);
			}
		});
		root.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				contextMenu.hide();
				parentController.getContextMenu().hide();
				MouseButton button = event.getButton();
				if(button==MouseButton.SECONDARY){
					System.out.println("Right Clicked timelineBar at: "+ event.getScreenX()+ " or scene:"+event.getSceneX());
					contextMenu.show(root, event.getScreenX(), event.getScreenY());
					seekPoint = parentController.getNewSeekPoint(event);
				}
				event.consume(); //Consumes the event so it wont go deeper down into the hierarchy 
			}
		});


	}

	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		relocate ( 
				(int) (p.getX()),
				(int) (p.getY())
				);
	}
	public long getStopPointPosition(){
		return (long)((localToParent(-10,25).getX()*scale));
	}

	/**
	 * When the scale changes we move the Stop to the new scaled location
	 * @param newScale
	 */
	public void scaleChanged(int newScale){
		System.out.println("StopPoint: scalechanged" );
		this.scale = newScale;
		System.out.println("StopPoint new GlobalTime: " + AdvancedScreen.getInstance().getScreenController().getGlobalTime() + " New Scale: " + newScale + " Gives you: " + (AdvancedScreen.getInstance().getScreenController().getGlobalTime()*scale)/1000);
		System.out.println("StopPoint: layoutX*scale = "+ getLayoutX()*scale);
		root.setLayoutX(((ge tLayoutX()-10)*scale)); //TODO: FIX
		initializeTooltip();
	}
	
	private String getStartTimeAsText(){
		long timeInSeconds = getStopPointPosition();
		long hours = timeInSeconds/3600;
		long minutes = (timeInSeconds % 3600) / 60;
		long seconds = timeInSeconds % 60;
	    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}
