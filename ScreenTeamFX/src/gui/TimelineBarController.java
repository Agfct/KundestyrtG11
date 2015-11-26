/**
 * 
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import modules.Event;

/**
 * @author Anders Lunde
 * The timelineBar is the Bar that is between the header and the scrollpain containing the timelines.
 * The timelineBar has both stopPointController and SeekerController as its children.
 */
public class TimelineBarController extends Pane {

	private FXMLLoader fxmlLoader;
	private AdvancedScreenController parentController;
	private Pane root = this;
	private TimelineBarController thisBarController = this;
	
	private ArrayList<StopPointController> stopPointControllers;

	//Last pressed rightClick on timelineBar
	private Point2D seekPoint;
	
	private SeekerController seeker;
	private final ContextMenu contextMenu = new ContextMenu();

	public TimelineBarController(AdvancedScreenController parentController){
		//Fetches the parent controller. In this case it is the controller in the advancedScreen class.'
		this.parentController = parentController;
		stopPointControllers = new ArrayList<StopPointController>();
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
		//Creates a clipping mask to hide timelineBar outside of bounds
		createClip();
		initializeMouse();

		seeker = new SeekerController(this);
		this.getChildren().add(seeker);

	}



	private void initializeMouse(){
		//Adds different right click options to the ContextMenu that pops up on mouse click.
		MenuItem moveSeeker = new MenuItem("MoveSeeker");
		MenuItem placeStop = new MenuItem("PlaceStop");
		contextMenu.getItems().addAll(moveSeeker,placeStop);
		moveSeeker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				moveSeeker(seekPoint);
			}
		});
		placeStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				StopPointController tempStopPoint = new StopPointController(thisBarController);
				root.getChildren().add(tempStopPoint);
				stopPointControllers.add(tempStopPoint);
				tempStopPoint.setStopPosition(seekPoint);
				tempStopPoint.toBack();
			}
		});
		root.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				contextMenu.hide();
				//            	parentController.getContextMenu().hide();
				MouseButton button = event.getButton();
				if(button==MouseButton.SECONDARY){
					contextMenu.show(root, event.getScreenX(), event.getScreenY());
					seekPoint = getNewSeekPoint(event);
				}
				event.consume(); //Consumes the event so it wont go deeper down into the hierarchy 
			}
		});


	}
	
	public void repaint(){
		ArrayList<Event> breakpoints = parentController.getCurrentSession().getBreakpoints();
		for(Event e : breakpoints){
			long timespot = e.getTime();
			StopPointController tempStopPoint = new StopPointController(thisBarController);
			root.getChildren().add(tempStopPoint);
			stopPointControllers.add(tempStopPoint);
			tempStopPoint.setStopPosition(timespot);
			tempStopPoint.toBack();
		}
	}
	
	public void removeAllBreakpoints(){
		while( 0<stopPointControllers.size() ){
			StopPointController spc = stopPointControllers.get(0);
			removeStopPoint(spc);
		}
	}

	/**
	 * Returns a new seek point based on a mouse click event.
	 * The seek point will be in the TimelineBarController's own local x,y coordinates
	 * and will always be placed on the top (y = 0). 
	 * @param event
	 * @return
	 */
	public Point2D getNewSeekPoint(MouseEvent event){
		return root.sceneToLocal(event.getSceneX(),0);
	}

	/**
	 * Creates a clip that decides the viewport of this Panes children
	 * The timelineBar that is outside of this clip will be hidden for the user.
	 */
	private void createClip(){
		Rectangle clipSize = new Rectangle(1000,1000);
		clipSize.setLayoutX(11);
		clipSize.setLayoutY(0);
		parentController.getTimelineBarContainer().setClip(clipSize);
	}


	public SeekerController getSeeker(){
		return seeker;
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

	public void scaleChanged(){
		double oldWidth = root.getPrefWidth();
		root.setPrefWidth(parentController.getCurrentSession().getSessionLength()*parentController.getScaleCoefficient()+22);
		seeker.scaleChanged(parentController.getScaleCoefficient());
		
		//Making a scale value based on the difference in root size instead of sending in the new scale
		double timelineBarValueScaleCoeff = (root.getPrefWidth()-22)/(oldWidth-22);
		for (StopPointController stopPoint : stopPointControllers) {
//			stopPoint.scaleChanged(parentController.getScaleCoefficient());
			stopPoint.scaleChangedCoeff(timelineBarValueScaleCoeff, parentController.getScaleCoefficient());
		}
	}

	public void moveSeeker(Point2D seekPoint){
		seeker.moveTo(seekPoint);
	}

	public void removeStopPoint(StopPointController stopPoint){
		stopPointControllers.remove(stopPoint);
		this.getChildren().remove(stopPoint);
	}

	public ContextMenu getContextMenu(){
		return contextMenu;
	}



}
