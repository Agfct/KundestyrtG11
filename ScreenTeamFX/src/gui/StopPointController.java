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
 *Controls the StopPoint.fxml . 
 *This is the red "pause"/"breakpoint" that appears in the timelineBar when the user right clicks and selects add stop point.
 *This class handles the rightClick for removing the stopPoint and also contains default methods for scaling itself when scale changes.
 *The stop point has the timelineBarController as its parent.
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
	
	//Hardcoded values
	//Values are taken from the StopPoint.fxml which default is 5x25
	private final int WIDTH = 5;
	private final int HEIGTH = 25;
	
	//The time in the modules is in miliseconds (int value) so we need to divide and multiply by 1000 to scale things right.
	private final int milisecs = 1000;
	
	//The point where the time is 0 (zero) is at pixel nr 13. The center of the stop pointer is at pixel nr 3.
	//So to calculate the time we always move the stopPointer 10 (13-3) pixels to the left.
	private final int PIXELSFROMCENTER = 10;
	
	//Using default of 5 in WIDTH the center is 3, 
	//so the stop pointer needs to go -2 to the left from where you put the mouse, 
	//this will put the 3rd pixel where your mouse is
	private final int CENTERING = 2;

	public StopPointController(TimelineBarController parent){
		parentController = parent;
		scale = parentController.getAdvancedScreenController().getScaleCoefficient();


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

	/**
	 * Sets the position of the stopPoint based on a point given by the TimelineBarController.
	 * This point is usually a mouse rightClick.
	 * @param point
	 */
	public void setStopPosition(Point2D point){
		relocateToPoint(new Point2D((point.getX()- CENTERING),0));
		initializeTooltip();
		System.out.println("Stop point point: " + this.getLayoutX());
		System.out.println("StopPoint, ToScale: "+ localToParent(-PIXELSFROMCENTER,HEIGTH).getX()/scale);
		long time = (long)(((getLayoutX()-PIXELSFROMCENTER)/scale)*milisecs);
		parentController.getAdvancedScreenController().getCurrentSession().addBreakpoint(time);

	}
	
	public void setStopPosition(long time){
		relocateToPoint(new Point2D((((time/milisecs))*scale+PIXELSFROMCENTER),0));
		initializeTooltip();
	}
	


	private void initializeTooltip(){
		//Sets the text of the tooltip
		mediaTooltip.setText(
						"PauseTime: "+getStartTimeAsText()+"\n"
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
				long time = (long)(((getLayoutX()-PIXELSFROMCENTER)/scale)*milisecs);
				parentController.getAdvancedScreenController().getCurrentSession().removeBreakpoint(time);
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
		return (long)((getLayoutX()-PIXELSFROMCENTER)/scale);
	}
	
	/**
	 * This method was made because stopPoint had no saved value in modules so it has to rescale based on a legth coeff from timelineBarController.
	 * @param newScaleCoeff
	 */
	public void scaleChangedCoeff(double newScaleCoeff, int newScale){
		this.scale = newScale;
		root.setLayoutX(((getLayoutX()-PIXELSFROMCENTER)*newScaleCoeff)+PIXELSFROMCENTER);
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
