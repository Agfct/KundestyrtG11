/**
 * 
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
	private Pane root = this;
	
	//Last pressed rightClick on timelineBar
	private Point2D seekPoint;
	private ArrayList<Canvas> currentListOfCanvases = new ArrayList<Canvas>();

	private SeekerController seeker;
	private final ContextMenu contextMenu = new ContextMenu();
	
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
	//Creates a clipping mask to hide timelineBar outside of bounds
	createClip();
	initializeMouse();
	
	seeker = new SeekerController(this);
	this.getChildren().add(seeker);

	}
	

	
	private void initializeMouse(){
		//Adds different right click options to the ContextMenu that pops up on mouse click.
		MenuItem moveSeeker = new MenuItem("MoveSeeker");
		contextMenu.getItems().addAll(moveSeeker);
		moveSeeker.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		        System.out.println("Remove MediaObject");
		        seeker.moveTo(seekPoint);
		    }
		});
		root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			 
            @Override
            public void handle(MouseEvent event) {
            	contextMenu.hide();
//            	parentController.getContextMenu().hide();
                MouseButton button = event.getButton();
                if(button==MouseButton.SECONDARY){
                    System.out.println("Right Clicked timelineBar at: "+ event.getScreenX()+ " or scene:"+event.getSceneX());
                    contextMenu.show(root, event.getScreenX(), event.getScreenY());
                    seekPoint = root.sceneToLocal(event.getSceneX(),0);
                }
                event.consume(); //Consumes the event so it wont go deeper down into the hierarchy 
            }
        });
		

	}
	
	/**
	 * Creates a clip that decides the viewport of this Panes children
	 * The timelineBar that is outside of this clip will be hidden for the user.
	 */
	private void createClip(){
		Rectangle clipSize = new Rectangle(1000,450);
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
		System.out.println("TimelineBar: Scaling size");
		root.setPrefWidth(parentController.getCurrentSession().getSessionLength()*parentController.getScaleCoefficient()+22);
		//TODO: move the seeker and scale it correctly
	}
	


}
