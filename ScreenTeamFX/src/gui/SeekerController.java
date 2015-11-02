/**
 * 
 */
package gui;

import java.io.IOException;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import modules.MainModuleController;


/**
 * @author Anders Lunde
 *
 */
public class SeekerController extends Pane{
	private FXMLLoader fxmlLoader;
	private TimelineBarController parentController;
	private Pane root = this;
	
	//scaling
	private int scale = 1;

	private Canvas seekerLine;
	private GraphicsContext seekergc;
	private int seekerLineHeight = 1000;
	private double unScaledX; //This X value is unafected by scale

	//Dragging:
	private EventHandler <DragEvent> mContextDragOver;
	private EventHandler <DragEvent> mContextDragDone;
	private EventHandler <DragEvent> mContextDragDropped;
	private Point2D mDragOffset = new Point2D (0.0, 0.0);

	public SeekerController(TimelineBarController parent){
		parentController = parent;
		
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("Seeker.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.setRoot(this);
			fxmlLoader.load();
		} catch (IOException e) {
			System.out.println("Failed to load Seeker FXML");
			e.printStackTrace();
		}

		initialize();
	}


	/**
	 * This method is ran after the controller is created in order to get node position values.
	 */
	public void initialize() {
		unScaledX = this.getLayoutX();
		buildNodeDragHandlers();
		initializeSeeker();
		drawSeekerLine();
	}

	/**
	 * Adds the seeker line to the root node of this controller
	 */
	public void initializeSeeker(){
		seekerLine = new Canvas(2,0);
		root.getChildren().add(seekerLine);
		seekerLine.toFront();
		//Centers the line
		seekerLine.relocate(11,0);
	}
	public void drawSeekerLine(){
		seekerLine.setHeight(seekerLineHeight);
		seekergc = seekerLine.getGraphicsContext2D() ;
		seekergc.setLineWidth(1.0);
		seekergc.moveTo(1+0.5, 25);
		seekergc.lineTo(1+0.5, seekerLineHeight);
		seekergc.stroke();
	}

	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		relocate ( 
				(int) (p.getX()),
				(int) (p.getY())
				);
	}

	/**
	 * Used by TimelineBarController when rightClicked and the user chooses to "move seeker to the right clicked position".
	 * @param pointX
	 */
	public void moveTo(Point2D pointX){
		System.out.println("Point: " + pointX);
		//Moves by -12 to set the pointer to the middle
		relocateToPoint(new Point2D(pointX.getX()- 12,0));
		seekerChanged();
	}
	
	private double getActuall0X(){
		return root.getLayoutX()-11;
	}
	
	public double getSeekerPosition(){
		return localToParent(0,25).getX();
	}
	
	public double getSeekerPositionMiddle(){
		System.out.println("[SeekerController] parent wiIDTH !! " + parentController.getRoot().getPrefWidth());
		double tempPos = getSeekerPosition();
		if(tempPos < 500){
			System.out.println("[SeekerController] Less than 500: " + tempPos);
			return 0;
		}else if (tempPos > 500 && tempPos < parentController.getRoot().getPrefWidth()-500){
			System.out.println("[SeekerController] between 500: " + tempPos);
			return tempPos - 500;
		}
		System.out.println("[SeekerController] NOTHING !! " + parentController.getRoot().getPrefWidth());
		return parentController.getRoot().getPrefWidth()-1022;
	}
	
	public void buildNodeDragHandlers() {


		/**
		 * This is the method for handling dragging the Seeker
		 */
		mContextDragOver = new EventHandler <DragEvent>() {

			@Override
			public void handle(DragEvent event) {		
//				System.out.println("[Seeker] Dragging over root");
				Pane timelineBarController = parentController.getRoot();
				Point2D p = timelineBarController.sceneToLocal(event.getSceneX(), event.getSceneY());
				Bounds mediaControllerRect;
				if(p.getX() - mDragOffset.getX() >= 0){
					mediaControllerRect = new BoundingBox(p.getX() - mDragOffset.getX(),0,
							25, 25);
				}else{
					mediaControllerRect = new BoundingBox(0,0,
							25, 25);
				}

				if (timelineBarController.getBoundsInLocal().contains(mediaControllerRect)) {
					event.acceptTransferModes(TransferMode.MOVE);
					relocateToPoint(new Point2D(mediaControllerRect.getMinX(),mediaControllerRect.getMinY()));
					//TODO: Local point in advanced sceen
					AdvancedScreen.getInstance().getScreenController().moveSeekerPopup(localToScene(0,25));
					AdvancedScreen.getInstance().getScreenController().ifSeekerIsOutsideThenScroll(localToScene(0,25));
				}
				event.consume();
			}
		};

		/**
		 * This is the method for handling dropping of the MediaObject
		 */
		mContextDragDropped = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {

				//				parentController.getRoot().setOnDragOver(null);
				//				parentController.getRoot().setOnDragDropped(null);
				//				
				

				event.setDropCompleted(true);

				event.consume();
			}
		};

		mContextDragDone = new EventHandler <DragEvent> () {

			@Override
			public void handle (DragEvent event) {
				System.out.println("[SeekerController] Drag DONE X is: " + localToParent(0,25).getX()/scale);

				parentController.getRoot().removeEventHandler(DragEvent.DRAG_OVER, mContextDragOver);
				parentController.getRoot().setOnDragOver(null);
				AdvancedScreen.getInstance().getScreenController().getMasterRoot().removeEventHandler(DragEvent.DRAG_OVER, mContextDragOver);
				AdvancedScreen.getInstance().getScreenController().getMasterRoot().setOnDragOver(null);
				parentController.getRoot().setOnDragDropped(null);
				parentController.getRoot().setOnDragDone(null);
				
				//Hiding the seekerPopup
				AdvancedScreen.getInstance().getScreenController().setSeekerPopoupVisibility(false);

				seekerChanged();

			}
		};

		/**
		 * When you drag the MediaObject it triggers setOnDragDetected
		 */
		setOnDragDetected ( new EventHandler <MouseEvent> () {

			@Override
			public void handle(MouseEvent event) {

				/* Drag was detected, start a drag-and-drop gesture */
				/* allow any transfer mode */
				System.out.println("[SeekerController] Drag event started");

				AdvancedScreen.getInstance().getScreenController().getMasterRoot().setOnDragOver (mContextDragOver);

				parentController.getRoot().setOnDragOver (mContextDragOver);
				parentController.getRoot().setOnDragDropped (mContextDragDropped);
				parentController.getRoot().setOnDragDone(mContextDragDone);

				//begin drag ops
				mDragOffset = new Point2D(event.getX(), event.getY());
				System.out.println("dragOffset with getX: " + mDragOffset);

				//TODO: TEST STUFF:
				System.out.println("relocate to point with sceneX with getX: " + (new Point2D(event.getSceneX(), event.getSceneY())));
				Pane timelineLinePane = parentController.getRoot();
				System.out.println("AnchorPane: getBoundsInLocal " + timelineLinePane.getBoundsInLocal());
				System.out.println("SeekerController Object: getBounds in parent" + getBoundsInParent());

				//The clipboard contains all content that are to be transfered in the drag
				ClipboardContent content = new ClipboardContent();

				//creating a container with all the data of the media object
				MediaObjectContainer container = new MediaObjectContainer();			
				//				container.addData ("type", timelineMediaObject.getParent().getType().toString());

				//Putting the data container onto the content
				content.put(MediaObjectContainer.DragNode, container);
				
				//Showing the seekerPopup to display temp globaltime
				AdvancedScreen.getInstance().getScreenController().setSeekerPopoupVisibility(true);
				
				//Pauses all timelines when you start to drag
				AdvancedScreen.getInstance().getScreenController().pauseAllTimelines();
				
				startDragAndDrop (TransferMode.MOVE).setContent(content);      

				event.consume();					
			}

		});

	}
	
	private void seekerChanged(){
		AdvancedScreen.getInstance().getScreenController().changeGlobalTime(getTempGlobalTime());
	}
	
	public long getTempGlobalTime(){
		return (long)((localToParent(0,25).getX()/scale)*1000);
	}
	public void placeSeeker( long newGlobalTime){
		root.setLayoutX((newGlobalTime*scale)/1000);
	}
	
	/**
	 * When the scale changes we move the seeker to the new scaled location
	 * @param newScale
	 */
	public void scaleChanged(int newScale){
		System.out.println("seeker: scalechanged");
		this.scale = newScale;
		root.setLayoutX((AdvancedScreen.getInstance().getScreenController().getGlobalTime()*scale)/1000);
	}
}
